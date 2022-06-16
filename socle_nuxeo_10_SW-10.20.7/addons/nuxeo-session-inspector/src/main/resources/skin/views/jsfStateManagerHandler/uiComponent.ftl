<@extends src="base.ftl">

<@block name="title">UIComponent ${componentId}</@block>

<@block name="body">

<div>

<h1><@block name="h1">UIComponent</@block></h1>

<@block name="compHeader" />

<@block name="compInfo">
  <h2>General Info</h2>

  <table class="info">
    <tr>
      <td class="labelColumn">Component Id</td>
      <td>${id}</td>
    </tr>
    <tr>
      <td class="labelColumn">Path</td>
      <td>${path}</td>
    </tr>
    <tr>
      <td class="labelColumn">Class</td>
      <td>${type}</td>
    </tr>
    <tr>
      <td class="labelColumn">Depth</td>
      <td>${depth}</td>
    </tr>
    <tr>
      <td class="labelColumn">Size</td>
      <td>${size}</td>
    </tr>
  </table>

</@block>

<@block name="compState">
  <h2>State Content</h2>

  <ul>
    <#list children as item>
      <li>${item}</li>
    </#list>
  </ul>

</@block>

<@block name="compMore" />

</div>

</@block>

</@extends>