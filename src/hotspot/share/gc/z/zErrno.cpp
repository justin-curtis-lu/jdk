/*
 * Copyright (c) 2015, 2025, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

#include "gc/z/zErrno.hpp"
#include "runtime/os.hpp"

#include <errno.h>
#include <string.h>

ZErrno::ZErrno()
  : _error(errno) {}

ZErrno::ZErrno(int error)
  : _error(error) {}

ZErrno::operator bool() const {
  return _error != 0;
}

bool ZErrno::operator==(int error) const {
  return _error == error;
}

bool ZErrno::operator!=(int error) const {
  return _error != error;
}

const char* ZErrno::to_string() const {
  return os::strerror(_error);
}
