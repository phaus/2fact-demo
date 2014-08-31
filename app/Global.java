import com.warrenstrange.googleauth.ICredentialRepository;

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
            Logger.info("Using " + loadedImpl.getClass());
        }
    }
}
