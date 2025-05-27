package sua.autonomouscar.mapeklite.adaptation.resources.sondas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;
import sua.autonomouscar.driving.interfaces.IDrivingService;
import sua.autonomouscar.infrastructure.OSGiUtils;
import sua.autonomouscar.infrastructure.driving.DrivingService;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.EFuncionConduccion;

public class SondaFuncionConduccion extends Probe implements ActionListener {
	
	public static String ID = "Sonda Funcion Conduccion";
	List<IDrivingService> drivingServices = null;

	public SondaFuncionConduccion(BundleContext context) {
		super(context, ID);
		this.logger.debug("Starting FC probe");
		drivingServices = OSGiUtils.getServices(context, IDrivingService.class);
		this.logger.debug(String.format("%s driving services found", drivingServices));
		
		for (IDrivingService drivingServiceInt : drivingServices) {
			DrivingService drivingService = (DrivingService) drivingServiceInt;
			this.logger.debug(String.format("Adding driving service: %s", drivingService));
			drivingService.addPropertyChangeListener(this);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == DrivingService.ACTIVE) {
			IDrivingService drivingService = (IDrivingService) e.getSource();
			
			if (drivingService.isDriving()) {
				EFuncionConduccion funcionConduccion = EFuncionConduccion.valueOf(drivingService.getClass().getSimpleName());
				this.reportMeasure(funcionConduccion);
			}
		}
	}
		
}
