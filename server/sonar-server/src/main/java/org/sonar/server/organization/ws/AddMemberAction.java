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
import org.sonar.api.server.ws.WebService.NewController;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.organization.OrganizationMemberDto;
import org.sonar.db.permission.OrganizationPermission;
import org.sonar.db.user.UserDto;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.user.UserSession;

import static java.lang.String.format;
import static org.sonar.server.organization.ws.OrganizationsWsSupport.PARAM_LOGIN;
import static org.sonar.server.organization.ws.OrganizationsWsSupport.PARAM_ORGANIZATION;
import static org.sonar.server.ws.KeyExamples.KEY_ORG_EXAMPLE_001;
import static org.sonar.server.ws.WsUtils.checkFound;
import static org.sonar.server.ws.WsUtils.checkFoundWithOptional;

public class AddMemberAction implements OrganizationsWsAction {
  private final DbClient dbClient;
  private final UserSession userSession;

  public AddMemberAction(DbClient dbClient, UserSession userSession) {
    this.dbClient = dbClient;
    this.userSession = userSession;
  }

  @Override
  public void define(NewController context) {
    WebService.NewAction action = context.createAction("add_member")
      .setDescription("Add a user as a member of an organization.<br>" +
        "Requires 'Administer System' permission on the specified organization.")
      .setSince("6.4")
      .setPost(true)
      .setInternal(true)
      .setHandler(this);

    action
      .createParam(PARAM_ORGANIZATION)
      .setDescription("Organization key")
      .setRequired(true)
      .setExampleValue(KEY_ORG_EXAMPLE_001);

    action
      .createParam(PARAM_LOGIN)
      .setDescription("User login")
      .setRequired(true)
      .setExampleValue("ray.bradbury");
  }

  @Override
  public void handle(Request request, Response response) throws Exception {
    String organizationKey = request.mandatoryParam(PARAM_ORGANIZATION);
    String login = request.mandatoryParam(PARAM_LOGIN);

    try (DbSession dbSession = dbClient.openSession(false)) {
      OrganizationDto organization = checkFoundWithOptional(dbClient.organizationDao().selectByKey(dbSession, organizationKey), "Organization '%s' is not found",
        organizationKey);
      UserDto user = checkFound(dbClient.userDao().selectByLogin(dbSession, login), "User '%s' is not found", login);

      userSession.checkPermission(OrganizationPermission.ADMINISTER, organization);

      OrganizationMemberDto organizationMember = new OrganizationMemberDto()
        .setOrganizationUuid(organization.getUuid())
        .setUserId(user.getId());

      dbClient.organizationMemberDao().select(dbSession, organization.getUuid(), user.getId())
        .ifPresent(o -> {
          throw BadRequestException.create(format("User '%s' is already a member of organization '%s'", user.getLogin(), organization.getKey()));
        });

      dbClient.organizationMemberDao().insert(dbSession, organizationMember);
      dbSession.commit();
    }

    response.noContent();
  }
}
