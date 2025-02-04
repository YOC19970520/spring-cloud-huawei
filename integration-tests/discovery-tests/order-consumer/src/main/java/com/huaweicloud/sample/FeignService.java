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
package com.huaweicloud.sample;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "price")
public interface FeignService {
  @PostMapping("/price")
  String getPrice(@RequestParam("id") Long id);

  @RequestMapping("/retry")
  String retry(@RequestParam(name = "invocationID") String invocationID);

  @RequestMapping("/retryMore")
  String retryMore(@RequestParam(name = "invocationID") String invocationID);

  @RequestMapping(value = "/faultInjection", produces = "application/json")
  @ResponseBody
  String faultInjection();

  @RequestMapping(value = "/faultInjectionModel", produces = "application/json")
  @ResponseBody
  PojoModel faultInjectionModel();

  @RequestMapping("/invocationContext")
  String invocationContext();

  @RequestMapping("/isolationForceOpen")
  String isolationForceOpen();

  @GetMapping("/feignInstanceBulkhead")
  String feignInstanceBulkhead();

  @RequestMapping("/loadbalance")
  String loadbalabce();

  @PostMapping("/testPostModel")
  PojoModel testPostModel(@RequestBody PojoModel model);
}
