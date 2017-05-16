package com.baozs.devkit.common;

import com.nikitakozlov.pury.Pury;
import com.nikitakozlov.pury.annotations.MethodProfiling;
import com.nikitakozlov.pury.annotations.MethodProfilings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by vashzhong on 2017/5/16.
 */

public class AppCommon {
    public static void init() {
        Pury.setLogger(new com.nikitakozlov.pury.Logger() {
            public Logger LOG = LoggerFactory.getLogger("PuryLog");
            @Override
            public void result(String s, String s1) {
                LOG.info("{}-{}", s, s1);
            }

            @Override
            public void warning(String s, String s1) {
                LOG.warn("{}-{}", s, s1);
            }

            @Override
            public void error(String s, String s1) {
                LOG.error("{}-{}", s, s1);
            }
        });
    }
}
