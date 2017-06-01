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
package com.okta.sdk.impl.authc.credentials

import com.okta.sdk.authc.credentials.ClientCredentials
import com.okta.sdk.authc.credentials.ClientCredentialsProvider
import org.testng.annotations.Test

import static org.testng.Assert.*

public class ClientCredentialsProviderChainTest {

    @Test
    public void credentialsReturnedInOrder() {

        ClientCredentials firstCredentials = buildClientCredentials()
        ClientCredentials secondCredentials = buildClientCredentials()

        ClientCredentialsProvider firstProvider = new ClientCredentialsProvider() {
            @Override
            ClientCredentials getClientCredentials() {
                return firstCredentials
            }
        }

        ClientCredentialsProvider secondProvider = new ClientCredentialsProvider() {
            @Override
            ClientCredentials getClientCredentials() {
                return secondCredentials
            }
        }

        ClientCredentialsProviderChain chain = new ClientCredentialsProviderChain() {};
        chain.addClientCredentialsProviders(firstProvider, secondProvider)

        ClientCredentials returned = chain.getClientCredentials()

        assertEquals(firstCredentials.baseUrl, returned.baseUrl)
        assertEquals(firstCredentials.secret, returned.secret)
        assertNotEquals(secondCredentials.baseUrl, returned.baseUrl)
        assertNotEquals(secondCredentials.secret, returned.secret)


    }

    @Test
    public void chainSearchedUntilCredentialsFound(){
        ClientCredentials thirdCredentials = buildClientCredentials()

        ClientCredentialsProvider firstProvider = new ClientCredentialsProvider() {
            @Override
            ClientCredentials getClientCredentials() {
                throw new IllegalStateException("can't find credentials")
            }
        }

        ClientCredentialsProvider secondProvider = new ClientCredentialsProvider() {
            @Override
            ClientCredentials getClientCredentials() {
                throw new IllegalStateException("can't find credentials")
            }
        }

        ClientCredentialsProvider thirdProvider = new ClientCredentialsProvider() {
            @Override
            ClientCredentials getClientCredentials() {
                return thirdCredentials
            }
        }

        ClientCredentialsProviderChain chain = new ClientCredentialsProviderChain() {};
        chain.addClientCredentialsProviders(firstProvider, secondProvider, thirdProvider)

        ClientCredentials returned = chain.getClientCredentials()

        assertEquals(thirdCredentials.baseUrl, returned.baseUrl)
        assertEquals(thirdCredentials.secret, returned.secret)

    }

    @Test
    public void emptyChainThrowsException(){
        try{
            ClientCredentialsProviderChain chain = new ClientCredentialsProviderChain() {};
            chain.getClientCredentials()
            fail("shouldn't get here")
        }
        catch (Throwable throwable){
            assertTrue(throwable instanceof IllegalStateException)
            assertEquals(throwable.getMessage(), "Unable to load credentials from any provider in the chain.")
        }
    }

    @Test
    public void exhaustedChainThrowsException(){
        try{
            ClientCredentialsProvider firstProvider = new ClientCredentialsProvider() {
                @Override
                ClientCredentials getClientCredentials() {
                    throw new IllegalStateException("can't find credentials")
                }
            }

            ClientCredentialsProvider secondProvider = new ClientCredentialsProvider() {
                @Override
                ClientCredentials getClientCredentials() {
                    throw new IllegalStateException("can't find credentials")
                }
            }

            ClientCredentialsProvider thirdProvider = new ClientCredentialsProvider() {
                @Override
                ClientCredentials getClientCredentials() {
                    throw new IllegalStateException("can't find credentials")
                }
            }

            ClientCredentialsProviderChain chain = new ClientCredentialsProviderChain() {};
            chain.addClientCredentialsProviders(firstProvider, secondProvider, thirdProvider)
            chain.getClientCredentials()
            fail("shouldn't get here")
        }
        catch (Throwable throwable){
            assertTrue(throwable instanceof IllegalStateException)
            assertEquals(throwable.getMessage(), "Unable to load credentials from any provider in the chain.")
        }
    }



    private static ClientCredentials buildClientCredentials() {

        String id = UUID.randomUUID().toString();
        String secret = UUID.randomUUID().toString();

        return new ClientCredentials() {


            String getBaseUrl() {
                return id
            }

            @Override
            String getSecret() {
                return secret
            }
        }
    }

}
