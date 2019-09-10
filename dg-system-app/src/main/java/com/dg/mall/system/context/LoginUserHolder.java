/**
 * Copyright 2018-2020 stylefeng & fengshuonan (sn93@qq.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dg.mall.system.context;


import com.dg.mall.system.api.context.LoginUser;

/**
 * <pre>
 * 当前登录用户的临时保存容器
 *
 *  说明：
 *     当OPEN_UP_FLAG标识在ThreadLocal里为true
 * </pre>
 *
 * @author fengshuonan
 * @Date 2018/7/3 下午5:29
 */
public class LoginUserHolder {

    private static final ThreadLocal<LoginUser> LONGIN_USER_HOLDER = new ThreadLocal<>();


    /**
     * 这个方法如果OPEN_UP_FLAG标识没开启，则会set失效
     *
     * @author fengshuonan
     * @Date 2018/7/4 上午11:09
     */
    public static void set(LoginUser abstractLoginUser) {

        LONGIN_USER_HOLDER.set(abstractLoginUser);

    }

    /**
     * 这个方法如果OPEN_UP_FLAG标识没开启，则会get值为null
     *
     * @author fengshuonan
     * @Date 2018/7/4 上午11:09
     */
    public static LoginUser get() {

        return LONGIN_USER_HOLDER.get();

    }

    /**
     * 删除保存的用户
     *
     * @author fengshuonan
     * @Date 2018/7/24 下午3:28
     */
    public static void remove() {

        LONGIN_USER_HOLDER.remove();
    }
}
