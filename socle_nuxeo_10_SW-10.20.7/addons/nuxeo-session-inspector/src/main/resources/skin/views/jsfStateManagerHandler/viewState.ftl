<@extends src="base.ftl">

<@block name="title">${viewId} State</@block>

<@block name="body">

<div>
  <h1>StateView</h1>

  <table id="stateInfo" class="info">
    <tr>
      <td class="labelColumn">View</td>
      <td>${viewId}</td>
    </tr>
    <tr>
      <td class="labelColumn">Sequence Id</td>
      <td>${sequenceId}</td>
    </tr>
    <tr>
      <td class="labelColumn">Session Size</td>
      <td>${dSessionSize?c}</td>
    </tr>
    <tr>
      <td class="labelColumn">Cumulated Size</td>
      <td>${cumulatedSize?c}</td>
    </tr>
    <tr>
      <td class="labelColumn">Max Depth</td>
      <td>${maxDepth?c}</td>
    </tr>
    <tr>
      <td class="labelColumn">Nb Branch</td>
      <td>${nbBranch?c}</td>
    </tr>
  </table>

  <a href="${Context.getBaseURL()}${This.path}/viewObjects/${viewId}/${sequenceId}">View objects</a>

  <h2>Paths</h2>
  <table id="stateDetail" class="tablesorter componentList">
    <thead>
      <tr>
        <th class="index">Index</th>
        <th class="depth">Depth</th>
        <th>Class</th>
        <th class="size">Size</th>
        <th class="nodePath">Path</th>
      </tr>
    </thead>
    <tbody>
      <#list nodeList as node>
      <tr class="nodeDetail">
        <td class="index">${node_index?c}</td>
        <td class="depth">${node.depth}</td>
        <td class="nodeClass">${node.type}</td>
        <td class="size">${node.size?c}</td>
        <td class="nodePath">
          <a href="${Context.getBaseURL()}${This.path}/${node.view}/${viewId}/${sequenceId}/${node.path}"
            target="_blank">
            ${node.path}
          </a>
        </td>
      </tr>
      </#list>
    </tbody>
  </table>

</div>

<script>
  jQuery(document).ready(function() {
      jQuery(".tablesorter").tablesorter();
  });
</script>

</@block>

</@extends>