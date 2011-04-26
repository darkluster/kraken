/*
 * Copyright 2011 Future Systems, Inc.
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
package org.krakenapps.dom.api;

import java.util.Collection;

import org.krakenapps.dom.model.NetworkAddress;

public interface NetworkAddressApi {
	Collection<NetworkAddress> getNetworkAddresses(int organizationId);

	NetworkAddress getNetworkAddress(int organizationId, int id);

	NetworkAddress createNetworkAddress(int organizationId, String name, int type, String address1, String address2);

	void updateNetworkAddress(int organizationId, int id, String name, int type, String address1, String address2);

	void removeNetworkAddress(int organizationId, int id);
}