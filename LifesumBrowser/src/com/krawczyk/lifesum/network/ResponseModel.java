/*
 * Copyright (C) 2012 The Andrino Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.krawczyk.lifesum.network;

import java.io.Serializable;
import java.util.List;

import com.krawczyk.lifesum.Food;

/**
 * @class ResponseModel
 * @author Pawel Krawczyk
 * @since 23-05-2014
 */
public class ResponseModel implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 7015962395660048433L;

    private List<Food> mList;

    /**
     * @return the list
     */
    public List<Food> getList() {
        return mList;
    }

    /**
     * @param list the list to set
     */
    public void setList(List<Food> list) {
        mList = list;
    }
}
