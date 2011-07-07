package roboguice.test;

import roboguice.application.RoboApplication;
import roboguice.service.RoboService;
import roboguice.inject.ContextScope;

import android.content.Context;
import android.test.ServiceTestCase;

import com.google.inject.Injector;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;

/**
 * Use RoboServiceTestCase to test services.
 *
 * An example:
 *
 * <code>
 * public class MyServiceTest extends RoboServiceTestCase<MyService, MyApplication> {
 *
 *     public MyServiceTest() {
 *         super(MyService.class);
 *     }
 *
 *     public void testStartable() {
 *         Intent startIntent = new Intent();
 *         startIntent.setClass(getContext(), MyService.class);
 *         startService(startIntent);
 *     }
 *
 *     public void testBindable() {
 *         Intent startIntent = new Intent();
 *         startIntent.setClass(getContext(), MyService.class);
 *         IBinder binder = bindService(startIntent);
 *         assertNotNull(binder);
 *     }
 * }
 * </code>
 *
 * Also, see the notes about your Application class below.
 *
 * @param <AppType> The type of your Application class.  This class must have a
 *                  constructor that accepts a Context argument and calls
 *                  {@link android.app.Application#attachBaseContext(android.content.Context)}
 */
public class RoboServiceTestCase<ServiceType extends RoboService, AppType extends RoboApplication> extends ServiceTestCase<ServiceType> {
    protected Injector injector;
    protected Context context;
    protected ContextScope scope;
    
    public RoboServiceTestCase(Class<ServiceType> serviceClass) {
        super(serviceClass);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        context = getContext();
        final Constructor constructor = applicationType().getConstructor(Context.class);
        final RoboApplication app = (RoboApplication)constructor.newInstance(context);
        injector = app.getInjector();
        scope = injector.getInstance(ContextScope.class);
        setApplication(app);

        scope.enter(context);
    }
    
    @Override
    protected void tearDown() throws Exception {
        scope.exit(context);
        
        super.tearDown();
    }

    protected Injector getInjector() {
        return injector;
    }

    protected Class<? extends RoboApplication> applicationType() {
        final ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<? extends RoboApplication>) parameterizedType.getActualTypeArguments()[1];
    }
}
