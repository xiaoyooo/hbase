/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hbase.ipc;

import com.google.protobuf.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.hbase.protobuf.ResponseConverter;

import java.io.IOException;

/**
 * Base class which provides clients with an RPC connection to
 * call coprocessor endpoint {@link Service}s
 */
@InterfaceAudience.Private
public abstract class CoprocessorRpcChannel implements RpcChannel, BlockingRpcChannel {
  private static Log LOG = LogFactory.getLog(CoprocessorRpcChannel.class);

  @Override
  public void callMethod(Descriptors.MethodDescriptor method,
                         RpcController controller,
                         Message request, Message responsePrototype,
                         RpcCallback<Message> callback) {
    Message response = null;
    try {
      response = callExecService(method, request, responsePrototype);
    } catch (IOException ioe) {
      LOG.warn("Call failed on IOException", ioe);
      ResponseConverter.setControllerException(controller, ioe);
    }
    if (callback != null) {
      callback.run(response);
    }
  }

  @Override
  public Message callBlockingMethod(Descriptors.MethodDescriptor method,
                                    RpcController controller,
                                    Message request, Message responsePrototype)
      throws ServiceException {
    try {
      return callExecService(method, request, responsePrototype);
    } catch (IOException ioe) {
      throw new ServiceException("Error calling method "+method.getFullName(), ioe);
    }
  }

  protected abstract Message callExecService(Descriptors.MethodDescriptor method,
                                  Message request, Message responsePrototype) throws IOException;
}
