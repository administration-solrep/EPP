<@extends src="base.ftl"> <@block name="title">${viewId}
Objects</@block> <@block name="body">

<div>
  <h1>Objects</h1>

  <table id="objectsInfo" class="info">
    <tr>
      <td class="labelColumn">View</td>
      <td>${viewId}</td>
    </tr>
    <tr>
      <td class="labelColumn">Sequence Id</td>
      <td>${sequenceId}</td>
    </tr>
  </table>

  <h2>Paths</h2>
  <table id="objectsDetail" class="tablesorter componentList">
    <thead>
      <tr>
        <th class="depth">Type</th>
        <th>Nb instance</th>
        <th>Size</th>
      </tr>
    </thead>
    <tbody>
      <#list objectList as object>
      <tr class="nodeDetail">
        <td>${object.type}</td>
        <td>${object.nbInstance?c}</td>
        <td>${object.cumulatedSize?c}</td>
      </tr>
      </#list>
    </tbody>
  </table>

</div>

<script>
  jQuery(document).ready(function() {
    jQuery(".tablesorter").tablesorter({sortList: [[2,1]]});
  });
</script>

</@block> </@extends>
