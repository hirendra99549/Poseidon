/*
 * Copyright 2016 Flipkart Internet, pvt ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.poseidon.validator;

import com.flipkart.poseidon.helper.ClassPathHelper;
import com.google.common.reflect.ClassPath;
import flipkart.lego.api.entities.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by shrey.garg on 15/07/16.
 */
public class BlocksValidator {
    private static final Logger logger = LoggerFactory.getLogger(BlocksValidator.class);

    public static void main(String[] args) {
        try {
            Set<ClassPath.ClassInfo> classInfos = ClassPathHelper.getPackageClasses(Thread.currentThread().getContextClassLoader(), Arrays.asList(args));
            System.out.println("Classes in ClassLoader: " + classInfos.size());
            Map<String, List<String>> errors = new HashMap<>();
            for (ClassPath.ClassInfo classInfo : classInfos) {
                Class clazz = Class.forName(classInfo.getName());
                if (Modifier.isAbstract(clazz.getModifiers())) {
                    continue;
                }

                if (DataSource.class.isAssignableFrom(clazz)) {
                    List<String> annotationErrors;
                    if (!(annotationErrors = AnnotationValidator.validateDataSource(clazz)).isEmpty()) {
                        errors.put(clazz.getName(), annotationErrors);
                    }
                }
            }

            if (!errors.isEmpty()) {
                logger.error(ValidatorUtils.getFormattedErrorMessages(errors));
                System.exit(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
