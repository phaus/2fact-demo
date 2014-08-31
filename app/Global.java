import com.warrenstrange.googleauth.ICredentialRepository;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ServiceLoader;

import play.Application;
import play.GlobalSettings;
import play.Logger;

/**
 * Created by philipp on 31.08.14.
 */
public class Global extends GlobalSettings {
    public void onStart(Application app) {
        Logger.info("@" + System.currentTimeMillis() + " Application has started");
        ServiceLoader<ICredentialRepository> impl = ServiceLoader.load(ICredentialRepository.class, app.classloader());
        for (ICredentialRepository loadedImpl : impl) {
            System.out.println("Using " + loadedImpl.getClass());
        }
        ClassLoader cl = app.classloader();

        URL[] urls = ((URLClassLoader) cl).getURLs();

        for (URL url : urls) {
            System.out.println(url.getFile());
        }
    }
}
