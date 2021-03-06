/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.boot.dubbo.actuate.endpoint;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.spring.ReferenceBean;
import org.apache.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Dubbo {@link Reference} Metadata {@link Endpoint}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Endpoint(id = "dubbo-references")
public class DubboReferencesMetadataEndpoint extends AbstractDubboEndpoint {

    @ReadOperation
    public Map<String, Map<String, Object>> references() {

        Map<String, Map<String, Object>> referencesMetadata = new LinkedHashMap<>();

        ReferenceAnnotationBeanPostProcessor beanPostProcessor = getReferenceAnnotationBeanPostProcessor();

        referencesMetadata.putAll(buildReferencesMetadata(beanPostProcessor.getInjectedFieldReferenceBeanMap()));
        referencesMetadata.putAll(buildReferencesMetadata(beanPostProcessor.getInjectedMethodReferenceBeanMap()));

        return referencesMetadata;

    }

    private Map<String, Map<String, Object>> buildReferencesMetadata(
            Map<InjectionMetadata.InjectedElement, ReferenceBean<?>> injectedElementReferenceBeanMap) {
        Map<String, Map<String, Object>> referencesMetadata = new LinkedHashMap<>();

        for (Map.Entry<InjectionMetadata.InjectedElement, ReferenceBean<?>> entry :
                injectedElementReferenceBeanMap.entrySet()) {

            InjectionMetadata.InjectedElement injectedElement = entry.getKey();

            ReferenceBean<?> referenceBean = entry.getValue();

            Map<String, Object> beanMetadata = resolveBeanMetadata(referenceBean);
            beanMetadata.put("invoker", resolveBeanMetadata(referenceBean.get()));

            referencesMetadata.put(String.valueOf(injectedElement.getMember()), beanMetadata);

        }

        return referencesMetadata;
    }

}
