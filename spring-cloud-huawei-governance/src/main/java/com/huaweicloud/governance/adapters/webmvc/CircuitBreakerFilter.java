/*

 * Copyright (C) 2020-2022 Huawei Technologies Co., Ltd. All rights reserved.

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
package com.huaweicloud.governance.adapters.webmvc;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.servicecomb.governance.handler.CircuitBreakerHandler;
import org.apache.servicecomb.governance.marker.GovernanceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huaweicloud.common.context.InvocationContext;
import com.huaweicloud.common.context.InvocationContextHolder;
import com.huaweicloud.common.util.HeaderUtil;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.decorators.Decorators.DecorateConsumer;
import io.vavr.CheckedConsumer;

public class CircuitBreakerFilter implements Filter {
  private static final Logger LOGGER = LoggerFactory.getLogger(BulkheadFilter.class);


  private static final Object EMPTY_HOLDER = new Object();

  private final CircuitBreakerHandler circuitBreakerHandler;

  public CircuitBreakerFilter(CircuitBreakerHandler circuitBreakerHandler) {
    this.circuitBreakerHandler = circuitBreakerHandler;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (!(request instanceof HttpServletRequest && response instanceof HttpServletResponse)) {
      chain.doFilter(request, response);
      return;
    }

    CheckedConsumer<Object> next = (v) -> chain.doFilter(request, response);
    DecorateConsumer<Object> decorateConsumer = Decorators.ofConsumer(next.unchecked());
    GovernanceRequest governanceRequest = convert((HttpServletRequest) request);

    try {
      CircuitBreaker circuitBreaker = circuitBreakerHandler.getActuator(governanceRequest);
      if (circuitBreaker != null) {
        decorateConsumer.withCircuitBreaker(circuitBreaker);
      }
      decorateConsumer.accept(EMPTY_HOLDER);
    } catch (Throwable e) {
      if (e instanceof CallNotPermittedException) {
        ((HttpServletResponse) response).setStatus(429);
        response.getWriter().print("circuitBreaker is open.");
        LOGGER.warn("circuitBreaker is open : {}",
            e.getMessage());
      } else {
        LOGGER.error("tttttttttt", e);
        throw e;
      }
    }
  }

  private GovernanceRequest convert(HttpServletRequest request) {
    GovernanceRequest govHttpRequest = new GovernanceRequest();
    govHttpRequest.setHeaders(HeaderUtil.getHeaders(request));
    govHttpRequest.setMethod(request.getMethod());
    govHttpRequest.setUri(request.getRequestURI());
    govHttpRequest.setServiceName(InvocationContextHolder
        .getOrCreateInvocationContext().getContext(InvocationContext.CONTEXT_MICROSERVICE_NAME));
    return govHttpRequest;
  }
}