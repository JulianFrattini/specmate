/**
 * generated by Xtext 2.17.1
 */
package org.xtext.specmate.ide;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.eclipse.xtext.util.Modules2;
import org.xtext.specmate.SpecDSLRuntimeModule;
import org.xtext.specmate.SpecDSLStandaloneSetup;
import org.xtext.specmate.ide.SpecDSLIdeModule;

/**
 * Initialization support for running Xtext languages as language servers.
 */
@SuppressWarnings("all")
public class SpecDSLIdeSetup extends SpecDSLStandaloneSetup {
  @Override
  public Injector createInjector() {
    SpecDSLRuntimeModule _specDSLRuntimeModule = new SpecDSLRuntimeModule();
    SpecDSLIdeModule _specDSLIdeModule = new SpecDSLIdeModule();
    return Guice.createInjector(Modules2.mixin(_specDSLRuntimeModule, _specDSLIdeModule));
  }
}
