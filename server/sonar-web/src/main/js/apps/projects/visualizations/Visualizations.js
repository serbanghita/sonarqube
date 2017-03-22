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
// @flow
import React from 'react';
import VisualizationsHeader from './VisualizationsHeader';
import Bugs from './Bugs';
import Vulnerabilities from './Vulnerabilities';
import CodeSmells from './CodeSmells';
import UncoveredLines from './UncoveredLines';
import DuplicatedBlocks from './DuplicatedBlocks';

export default class Visualizations extends React.PureComponent {
  props: {
    onProjectOpen: (string) => void,
    onVisualizationChange: (string) => void,
    projects?: Array<*>,
    visualization: string
  };

  renderVisualization(projects: Array<*>) {
    const visualizationToComponent = {
      bugs: Bugs,
      vulnerabilities: Vulnerabilities,
      code_smells: CodeSmells,
      uncovered_lines: UncoveredLines,
      duplicated_blocks: DuplicatedBlocks
    };
    const Component = visualizationToComponent[this.props.visualization];

    return Component
      ? <Component onProjectOpen={this.props.onProjectOpen} projects={projects} />
      : null;
  }

  render() {
    const { projects } = this.props;

    return (
      <div className="boxed-group projects-visualizations">
        <VisualizationsHeader
          onVisualizationChange={this.props.onVisualizationChange}
          visualization={this.props.visualization}
        />
        <div className="projects-visualization">
          <div>
            {projects != null && this.renderVisualization(projects)}
          </div>
        </div>
      </div>
    );
  }
}
