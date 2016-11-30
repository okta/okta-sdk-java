/*!
 * Copyright (c) 2015-2016, Okta, Inc. and/or its affiliates. All rights reserved.
 * The Okta software accompanied by this notice is provided pursuant to the Apache License, Version 2.0 (the "License.")
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.okta.sdk.framework;

import java.util.List;

public class PagedResults<T> {

    private ApiResponse<List<T>> apiResponse;

    public PagedResults(ApiResponse<List<T>> apiResponse) {
        this.apiResponse = apiResponse;
    }

    public boolean isLastPage() {
        return !apiResponse.getLinks().containsKey("next");
    }

    public boolean isFirstPage() {
        return apiResponse.getLinks().containsKey("previous");
    }

    public String getNextUrl() {
        return apiResponse.getLinks().get("next").getHref();
    }

    public List<T> getResult() {
        return apiResponse.getResponseObject();
    }

    public ApiResponse<List<T>> getApiResponse() {
        return apiResponse;
    }

    public void setApiResponse(ApiResponse<List<T>> apiResponse) {
        this.apiResponse = apiResponse;
    }
}
