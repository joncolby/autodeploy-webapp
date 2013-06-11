<%@ page contentType="text/html;charset=UTF-8" %>
<div class="container">
<div class="left">
	<div class="selectList teams">
		<h3>Teams</h3>
		<ul>
		<g:each in="${teams}" var="team">
		   <li url="<g:createLink action="plans" id="${team.id}"/>" itemId="${team.id}">${team.fullName} (${team.planCount} plans)</li>
		</g:each>
		</ul>
	</div>
	<div class="selectList deploymentPlans">
		<h3>DeploymentPlans</h3>
		<ul></ul>
	</div>
</div>
<div class="right">
	<div class="selectList applications">
		<h3>Applications<a class="none" href="#">select none</a><a class="all" href="#">select all</a></h3>
        <div class="filterBox">
            <input id="applicationsFilterField" type="text" placeholder="Type here to start filtering ..."/>
            <input id="clearFilterBtn" type="button" value="all"/>
            <input id="showSelectedBtn" type="button" value="selected"/>
        </div>
		<ul></ul>
	</div>
	<div class="details">
			<p name="created">
				<label>Created at</label>
				<span></span>
			</p>
			<p name="modified">
				<label>Last modified</label>
				<span></span>
			</p>
			<p name="contribution">
				<label>Contribution</label>
				<span></span>
			</p>
			<p name="ticket">
				<label>Ticket</label>
				<span></span>
			</p>
			<p name="name">
				<label>Name</label>
				<span></span>
			</p>
			<p name="requiresDatabaseChanges">
				<label>Requires database changes</label>
				<span></span>
			</p>
			<p name="requiresPropertyChanges">
				<label>Requires property changes</label>
				<span></span>
			</p>
			<p class="actions">
			<input type="submit" name="edit" value="Edit">
            <input type="submit" name="delete" value="Delete">
			<input type="submit" name="cancel" value="Cancel">
			</p>
			<p class="revisionBox">
			<label>Revision</label>
			<input id="test" type="text" name="revision"/>
			<input type="submit" url="<g:createLink action="addToQueue" />" value="Assign to Queue"/>
			</p>
	</div>
</div>
</div>