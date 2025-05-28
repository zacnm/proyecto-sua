package sua.autonomouscar.driving.l3.citychauffer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import sua.autonomouscar.driving.l3.citychauffer.Activator;

public class Activator implements BundleActivator {

	private static BundleContext context;
	protected L3_CityChaufferARC drivingFunctionARC = null;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		this.drivingFunctionARC = new L3_CityChaufferARC(bundleContext);
		this.drivingFunctionARC.start();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		this.drivingFunctionARC.stop();
		Activator.context = null;
	}
}