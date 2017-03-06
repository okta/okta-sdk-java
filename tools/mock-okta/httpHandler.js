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
/* eslint no-param-reassign:0 */

const util = require('./util');

const httpHandler = module.exports;

const handler = (opts, req, res) => {
  
  if (req.url === '/mock/unused-tapes') {
    res.end(util.prettyJSON(util.getTapeDetails(opts.tapesDir, opts.unusedTapes)));
    return Promise.resolve();
  }

  return util.standardizeRequest(req)
  .then((standardReq) => {
    // Track used tapes
    const setHeader = res.setHeader.bind(res);
    res.setHeader = (key, value) => {
      if (key === 'x-yakbak-tape') {
        opts.unusedTapes.delete(value);
      }
      setHeader(key, value);
    };
    
    const end = res.end.bind(res);
    res.end = (msg) => {
      if (msg === 'Recording Disabled') {
        // Modify the message to be more informative
        // Attempt to get the x-test-description and map it to an existing tape
        const description = standardReq.headers['x-test-description'];
        if (description) {
          const possibleTapes = opts.tapeDetails.filter((tapeDetail) => {
            return req.method === tapeDetail.method &&
              description === tapeDetail.headers['x-test-description'];
          });
          msg = util.prettyJSON({
            req: {
              headers: req.headers,
              method: req.method,
              url: req.url,
            },
            standardReq: {
              headers: standardReq.headers,
              method: standardReq.method,
              url: standardReq.url,
            },
            possibleTapes
          });
        } else {
          msg = 'Missing the x-test-description header';
        }
        console.log(msg);
      }
      end(msg);
    }

    opts.proxy(standardReq, res);
  });
};

httpHandler.create = opts => handler.bind(null, opts);
