/*
 * SonarQube
 * Copyright (C) 2009-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.sonar.server.organization.ws;

import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;

import static org.sonar.server.es.SearchOptions.MAX_LIMIT;

public class SearchMembersAction implements OrganizationsWsAction {
  @Override
  public void define(WebService.NewController context) {
    WebService.NewAction action = context.createAction("search_members")
      .setResponseExample(getClass().getResource("search_members-example.json"))
      .setSince("6.4")
      .setInternal(true)
      .setHandler(this);

    action.addSelectionModeParam();
    action.addSearchQuery("freddy", "names", "logins");
    action.addPagingParams(50, MAX_LIMIT);

    action.createParam("organization")
      .setDescription("Organization key")
      .setInternal(true)
      .setRequired(false);
  }

  @Override
  public void handle(Request request, Response response) throws Exception {
    // TODO
  }
}