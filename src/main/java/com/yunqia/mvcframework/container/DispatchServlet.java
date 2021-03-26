package com.yunqia.mvcframework.container;

import com.yunqia.mvcframework.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 该类模拟spring启动时扫描所有的类，将其依赖注入并保存到map中，
 * 后期的所有请求都根据该类进行分发到具体的类中
 *
 * @author 徐霁
 *
 * 微信/QQ：1198435103
 *
 */
public class DispatchServlet extends HttpServlet {

    //存储所有的扫描类文件
    private Map<String, Object> map = new HashMap();
    //存储所有的方法路径映射
    private Map<String, Object> mapping = new HashMap<String, Object>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("请求get");
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("请求POST");
        try {
            doDispatch(req, resp);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            resp.getWriter().write("500 exception" + Arrays.toString(e.getStackTrace()));
        } catch (InstantiationException e) {
            e.printStackTrace();
            resp.getWriter().write("500 exception" + Arrays.toString(e.getStackTrace()));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            resp.getWriter().write("500 exception" + Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * 所有的求情都通过该类处理，转发给相应的mapping映射方法处理
     * @param req
     * @param resp
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException, InvocationTargetException, IllegalAccessException, InstantiationException {
//        String requestURI = req.getRequestURI();
        String requestURI = req.getServletPath();

        //当前请求若不存在直接返回404
        if (!mapping.containsKey(requestURI)) {
            resp.getWriter().write("404 not found!");
            return;
        }
        Method method = (Method)mapping.get(requestURI);
        //执行类中的方法
        Class<?> declaringClass = method.getDeclaringClass();
        Object o = mapping.get(declaringClass.getName());
        method.invoke(o, new Object[]{req,resp});
    }

    /**
     * 完成spring容器初始化工作，将所有的类加载到容器中
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("初始化容器");
        InputStream is = null;
        try {
            //加载配置项下所有的包
            Properties properties = new Properties();
            //加载配置文件
            String contextConfigLocation = config.getInitParameter("contextConfigLocation");
            is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
            properties.load(is);
            String scanPackage = properties.getProperty("scan.package");
            doScanner(scanPackage);
            for (String className : map.keySet()) {
                //这一步判断似乎没有什么作用
                if (!className.contains(".")) {
                    continue;
                }
                //通过反射拿到class对象
                Class<?> aClass = Class.forName(className);
                //先处理@controller注解标注过的对象
                if (aClass.isAnnotationPresent(Controller.class)) {
                    mapping.put(className, aClass.newInstance());

                    String baseUrl = "";
                    //Controller类上可能存在@RequestMapping注解，将路径地址获取出来进行前缀拼接
                    if (aClass.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping requestMapping = aClass.getAnnotation(RequestMapping.class);
                        baseUrl = requestMapping.value();
                    }

                    //拿到当前类的所有方法
                    Method[] methods = aClass.getDeclaredMethods();
                    for (Method method : methods) {
                        if (!method.isAnnotationPresent(RequestMapping.class)) {
                            continue;
                        }
                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                        String url = (baseUrl + "/" + requestMapping.value()).replaceAll("/+", "/");
                        mapping.put(url, method);
                    }
                //处理被@Service标注过的类
                } else if (aClass.isAnnotationPresent(Service.class)){
                    String beanName = "";
                    Service service = aClass.getAnnotation(Service.class);
                    beanName = service.value();
                    if ("".equals(beanName)) {
                        beanName = aClass.getName();
                    }
                    Object o = aClass.newInstance();
                    mapping.put(beanName, o);
                    //找出所有当前类已经实现的接口
                    for (Class<?> anInterface : aClass.getInterfaces()) {
                        mapping.put(anInterface.getName(), o);
                    }
                //处理被@Component注解标注过的类
                } else if (aClass.isAnnotationPresent(Component.class)) {
                    String beanName = "";
                    Component component = aClass.getAnnotation(Component.class);
                    beanName = component.value();
                    if ("".equals(beanName)) {
                        beanName = aClass.getName();
                    }
                    mapping.put(beanName, aClass.newInstance());
                } else {
                    continue;
                }
            }

            //处理依赖注入，也就是将每个类中通过@AutoWired注解标注过的类加载进来
            for (Object value : mapping.values()) {
                if (value == null) {
                    continue;
                }
                Class<?> aClass = value.getClass();
                if (aClass.isAnnotationPresent(Controller.class)) {

                    Field[] fields = aClass.getDeclaredFields();
                    for (Field field : fields) {
                        if (field.isAnnotationPresent(Autowired.class)) {
                            String beanName = "";
                            Autowired autowired = field.getAnnotation(Autowired.class);
                            beanName = autowired.value();
                            if ("".equals(beanName)) {
                                beanName = field.getType().getName();
                            }
                            field.setAccessible(true);
                            try {
                                //有点迷惑
                                field.set(mapping.get(aClass.getName()), mapping.get(beanName));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 扫描指定路径下的所有文件
     * @param scanPackage
     */
    public void doScanner(String scanPackage){
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File files = new File(url.getFile());
        for (File file : files.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                //只保留路劲与文件名称
                String fileName = file.getName();
                String className = (scanPackage + "." + fileName.replace(".class", ""));
                map.put(className, null);
            }
        }
    }

}
