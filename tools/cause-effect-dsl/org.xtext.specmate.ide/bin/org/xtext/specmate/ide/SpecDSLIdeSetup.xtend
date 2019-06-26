/*
 * generated by Xtext 2.17.1
 */
package org.xtext.specmate.ide

import com.google.inject.Guice
import org.eclipse.xtext.util.Modules2
import org.xtext.specmate.SpecDSLRuntimeModule
import org.xtext.specmate.SpecDSLStandaloneSetup

/**
 * Initialization support for running Xtext languages as language servers.
 */
class SpecDSLIdeSetup extends SpecDSLStandaloneSetup {

	override createInjector() {
		Guice.createInjector(Modules2.mixin(new SpecDSLRuntimeModule, new SpecDSLIdeModule))
	}
	
}
