/*
 * Copyright 2014 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.sdk.impl.authc;

import java.util.Map;

/**
 * @since 1.0.RC
 */
public interface HttpServletRequestWrapper {

    public Class getHttpServletRequestClass();

    public Object getHttpServletRequest();

    public String getHeader(String headerName);

    public String getMethod();

    public Map<String, String[]> getParameterMap();
    
    public String getParameter(String parameterName);

}
