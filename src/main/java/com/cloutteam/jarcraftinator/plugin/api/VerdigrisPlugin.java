package com.cloutteam.jarcraftinator.plugin.api;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface VerdigrisPlugin {

    String name();
    String version();
    String author();
    String description();

}
