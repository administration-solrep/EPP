<@extends src="views/jsfStateManagerHandler/uiComponent.ftl">

<@block name="title">UIALiasHolder ${aliasId}</@block>

<@block name="h1">UIALiasHolder</@block>

<@block name="compHeader">

  <table class="info">
    <tr>
      <td class="labelColumn">Alias Id</td>
      <td>${aliasId}</td>
    </tr>
    <tr>
      <td class="labelColumn">Mapper Size</td>
      <td>${mapperSize?c}</td>
    </tr>
  </table>

  <h2>Variables</h2>

  <table id="aliasDetail" class="tablesorter componentList">
    <thead>
      <tr>
        <th>Key</th>
        <th>Value</th>
      </tr>
    </thead>
    <tbody>
      <#list variables as variable>
      <tr class="variableDetail">
        <td>${variable.key}</td>
        <td>${variable.value.getExpressionString()}</td>
      </tr>
      </#list>
    </tbody>
  </table>

</@block>

</@extends>