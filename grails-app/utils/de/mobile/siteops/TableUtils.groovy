package de.mobile.siteops

class TableUtils {

	static def addActions(def data, def g) {
		data['actions'] = []
			data['actions'] += [title: 'Edit', type: 'edit', action: g.createLink(action: 'ajaxEdit', id: data.id)]
			data['actions'] += [title: 'Delete', type: 'remove', action: g.createLink(action: 'ajaxDelete', id: data.id)]

		return data
	}
	
}

