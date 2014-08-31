package io.collap.controller.provider;

import io.collap.bryg.environment.Environment;

public interface BrygDependant extends Dependant {

    public void setBryg (Environment environment);

}
