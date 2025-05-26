package autonomouscar.mapeklite.adaptation.resources.rules;

import org.osgi.framework.BundleContext;

import autonomouscar.mapeklite.adaptation.resources.enums.FuncionConduccion;
import autonomouscar.mapeklite.adaptation.resources.enums.NivelAutonomia;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.structures.systemconfiguration.interfaces.IRuleComponentsSystemConfiguration;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.AdaptationRule;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.exceptions.analyzing.RuleException;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.SystemConfigurationHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.structures.systemconfiguration.interfaces.IRuleSystemConfiguration;
import es.upv.pros.tatami.osgi.utils.interfaces.ITimeStamped;
import sua.autonomouscar.interfaces.ERoadType;

public class HighwayToTrafficJamTransitionAdaptationRule extends AdaptationRule {
	
	public static String ID = "Regla Transición de Highway a Traffic Jam";
	
	IKnowledgeProperty kpNivelAutonomia = null;
	IKnowledgeProperty kpFuncionConduccion = null;
	IKnowledgeProperty kpTipoVia = null;

	public HighwayToTrafficJamTransitionAdaptationRule(BundleContext context) {
		super(context, ID);
		this.setListenToKnowledgePropertyChanges("nivel-autonmia");
		this.setListenToKnowledgePropertyChanges("funcion-conduccion");
		
		kpNivelAutonomia = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("nivel-autonmia");
		kpFuncionConduccion = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("funcion-conduccion");
		kpTipoVia = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("tipo-via");
	}

	@Override
	public boolean checkAffectedByChange(IKnowledgeProperty property) {
		if (kpNivelAutonomia == null || kpNivelAutonomia.getValue() == null ||
				kpFuncionConduccion == null || kpFuncionConduccion.getValue() == null ||
				kpTipoVia == null || kpTipoVia.getValue() == null) {
			logger.trace("Required Knowledge property not set. Not executing the rule ...");
			
			return false;
		}
		
		return true;
	}

	@Override
	public IRuleSystemConfiguration onExecute(IKnowledgeProperty property) throws RuleException {
		NivelAutonomia nivelAutonomia = (NivelAutonomia) kpNivelAutonomia.getValue();
		FuncionConduccion funcionConduccion = (FuncionConduccion) kpFuncionConduccion.getValue();
		ERoadType tipoVia = (ERoadType) kpTipoVia.getValue();
		
		if (nivelAutonomia == NivelAutonomia.AutomatizacionParcial && funcionConduccion == FuncionConduccion.L3_TrafficJamChauffer && tipoVia != ERoadType.CITY) {
			return this.configuracionSistemaActivarTrafficJamChauffer();
		} else {
			throw new RuleException("Cannot understand knowledge", "Not executing");
		}
	}

	protected IRuleComponentsSystemConfiguration configuracionSistemaActivarTrafficJamChauffer() {
		IRuleComponentsSystemConfiguration nextSystemConfiguration = 
				SystemConfigurationHelper.createPartialSystemConfiguration(this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());
		
		SystemConfigurationHelper.componentToRemove(nextSystemConfiguration, "driving.L3.HighwayChauffer", "1.0.0");
		SystemConfigurationHelper.componentToAdd(nextSystemConfiguration, "driving.L3.TrafficJamChauffer", "1.0.0");	
		
		return nextSystemConfiguration;		
		
	}
}
