/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ssiu.ucp.flink.core.bean;

import com.typesafe.config.Optional;

import javax.annotation.Nullable;

public class SchemaInfo {

    private String name;

    private String info;

    // use info as a type of expr
    @Optional
    private boolean Expr = false;

    @Optional
    private boolean primary = false;

    @Optional
    private boolean meta = false;

    // How to generate watermark from
    @Optional
    private @Nullable String watermark;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isExpr() {
        return Expr;
    }

    public void setExpr(boolean expr) {
        Expr = expr;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public boolean isMeta() {
        return meta;
    }

    public void setMeta(boolean meta) {
        this.meta = meta;
    }

    @Nullable
    public String getWatermark() {
        return watermark;
    }

    public void setWatermark(@Nullable String watermark) {
        this.watermark = watermark;
    }
}
