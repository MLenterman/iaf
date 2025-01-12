/*
   Copyright 2023 WeAreFrank!

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package nl.nn.adapterframework.management.bus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ClassUtils;

import lombok.Setter;

/**
 * Allows the creation of outbound integration gateways.
 */
public class OutboundGatewayFactory<T> implements InitializingBean, ApplicationContextAware, FactoryBean<IntegrationGateway<T>> {

	private Logger log = LogManager.getLogger(this);
	private @Setter ApplicationContext applicationContext;
	private IntegrationGateway<T> gateway;

	private @Setter String gatewayClassname = LocalGateway.class.getCanonicalName();

	@Override
	@SuppressWarnings("unchecked")
	public void afterPropertiesSet() throws Exception {
		Class<?> gatewayClass = ClassUtils.resolveClassName(gatewayClassname, applicationContext.getClassLoader());
		if(!IntegrationGateway.class.isAssignableFrom(gatewayClass)) {
			throw new IllegalArgumentException("gateway ["+gatewayClassname+"] does not implement type IntegrationGateway");
		}

		gateway = (IntegrationGateway<T>) applicationContext.getAutowireCapableBeanFactory().createBean(gatewayClass, AutowireCapableBeanFactory.AUTOWIRE_NO, false);
		log.info("created gateway [{}]", gateway);
	}

	@Override
	public IntegrationGateway<T> getObject() {
		return gateway;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class<? extends IntegrationGateway> getObjectType() {
		return (this.gateway != null ? this.gateway.getClass() : IntegrationGateway.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
