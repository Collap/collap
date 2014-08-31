package io.collap.bryg;

import io.collap.StandardDirectories;
import io.collap.bryg.loader.FileSourceLoader;
import io.collap.plugin.Module;

import java.io.File;

public class ModuleSourceLoader extends FileSourceLoader {

    public ModuleSourceLoader (Module module) {
        super (new File (StandardDirectories.resourceCache, module.getName () + "/template"));
    }

}
