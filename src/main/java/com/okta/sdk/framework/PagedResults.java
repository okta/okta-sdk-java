/*!
 * Copyright (c) 2015-2017, Okta, Inc. and/or its affiliates. All rights reserved.
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

    /**
     * List of Objects from the API response.
     */
    private ApiResponse<List<T>> apiResponse;

    /**
     * Constructor for the PagedResults object.
     * @param apiResponse {@link ApiResponse}
     */
    public PagedResults(ApiResponse<List<T>> apiResponse) {
        this.apiResponse = apiResponse;
    }

    /**
     * Returns True if on the last page.
     * @return {@link Boolean}
     */
    public boolean isLastPage() {
        return !apiResponse.getLinks().containsKey("next");
    }

    /**
     * Returns True if on the first page.
     * @return {@link Boolean}
     */
    public boolean isFirstPage() {
        return apiResponse.getLinks().containsKey("previous");
    }

    /**
     * Returns value of the next URL.
     * @return {@link String}
     */
    public String getNextUrl() {
		if (apiResponse.getLinks().get("next")!=null)
			return apiResponse.getLinks().get("next").getHref();
		return null;
    }

    /**
     * Returns the list response.
     * @return {@link List}
     */
    public List<T> getResult() {
        return apiResponse.getResponseObject();
    }

    /**
     * Returns the entire response object.
     * @return {@link ApiResponse}
     */
    public ApiResponse<List<T>> getApiResponse() {
        return apiResponse;
    }

    /**
     * Sets the API response.
     * @param apiResponse {@link ApiResponse}
     */
    public void setApiResponse(ApiResponse<List<T>> apiResponse) {
        this.apiResponse = apiResponse;
    }
}
