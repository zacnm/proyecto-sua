
package sua.autonomouscar.driving.l3.citychauffer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;
	protected L3_CityChauffer drivingFunction = null;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		System.out.println("Starting L3 City Chauffer Driving Function ...");
		this.drivingFunction = new L3_CityChauffer(bundleContext, "L3_CityChauffer");
		this.drivingFunction.registerThing();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		if ( this.drivingFunction != null )
			system.out.println("Stopping L3 City Chauffer Driving Function ...");
			this.drivingFunction.unregisterThing();
		
		Activator.context = null;
	}

}
