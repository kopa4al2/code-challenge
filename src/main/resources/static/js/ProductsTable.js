const HEADER_STATES = {
    INITIAL: 'initial',
    ASCENDING: 'ASC',
    DESCENDING: 'DESC',
}

class ProductsTable {
    HEADERS = ['Name', 'Category', 'Description', 'Quantity', 'Created date', 'Last modified date', 'Actions'];
    HEADERS_PROPERTIES = {
        Name: 'name',
        Category: 'category',
        Description: 'description',
        Quantity: 'quantity',
        'Created date': 'dateCreated',
        'Last modified date': 'lastModifiedDate'
    }
    // For Each header its current state (do we sort by it and if yes is its ascending or descending)
    headerStates = {};

    container;

    onSortChange;

    onOrder;

    onDelete;

    onUpdate;
    // object with key productId and value boolean (did update or not)
    didUpdate = {};

    /**
     * Create a new object given a container in which to render the data
     * and onSortChange callback when changing sort by / sort direction
     * @param tableContainer html element in which to render the table
     * @param onSortChange callback invoked when changing sort
     * @param onOrder callback invoked when ordering a product,
     *        callback params are product id and unvalidated quantity
     * @param onDelete callback invoked when deleting a product,
     *        callback params is only product id
     * @param onUpdate callback invoked when updating a product,
     *        callback params are productId and object with fields to update
     */
    constructor(tableContainer, onSortChange, onOrder, onDelete, onUpdate) {
        this.container = tableContainer;
        this.onSortChange = onSortChange;
        this.onOrder = onOrder;
        this.onDelete = onDelete;
        this.onUpdate = onUpdate;

        this.HEADERS.forEach(header => {
            this.headerStates[header] = HEADER_STATES.INITIAL
        });

        this.setHeaders();
    }

    setHeaders() {
        this.elementHeaders = this.HEADERS.map((header, index) => `
            <div
                 class="cell header-cell ${this.headerStates[header]}" 
                 data-header="${header}"
                 style="grid-row-start: 1; grid-column-start: ${index + 1}"
             >
                ${header}
                <svg class="sort-order" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 26 26" fill="black" width="24px" height="24px"><path d="M0 0h24v24H0z" fill="none"/><path d="M7 10l5 5 5-5z"/></svg>
            </div>
        `).join('');
    }

    addEventListeners() {
        document.querySelectorAll('.header-cell').forEach(header => {
            const { header: headerName } = header.dataset;
            if (headerName === 'Actions') return;
            header.addEventListener('click', () => {
                const currentState = this.headerStates[headerName];
                const nextState = this.getNextState(currentState);
                this.headerStates[headerName] = nextState;
                header.classList.remove(currentState);

                header.classList.add(nextState)
                this.onSortChange(this.mapStatesToSort());
            })
        })

        document.querySelectorAll('.action-btn').forEach(btn => {
            const { id, action } = btn.dataset;
            btn.addEventListener('click', () => {
                if (action === 'update') {
                    const allEditableSelector = $(`.editable-${id}`);
                    // Show tooltip that the user did not update the fields
                    if (!this.didUpdate[id]) {
                        allEditableSelector.tooltip({ title: 'Click on any of these fields to change them ' });
                        allEditableSelector.tooltip('enable');
                        allEditableSelector.tooltip('show');
                        setTimeout(() => {
                            allEditableSelector.tooltip('disable');
                            allEditableSelector.tooltip('hide');
                        }, 2000);
                        return;
                    }
                    const nodes = document.querySelectorAll(`.description-${id}, .name-${id}, .category-${id}`);
                    const newValues = [...nodes].reduce((acc, node) => {
                        const value = node.textContent;
                        // Remove the cell class and what is left is the fieldName-productId class
                        // split it by `-` and get the first one
                        const fieldName = [...node.classList].filter(c => c !== 'cell')[0].split('-')[0];
                        acc[fieldName] = value;
                        return acc;
                    }, {});
                    allEditableSelector.tooltip('disable');
                    this.didUpdate[id] = false;
                    this.onUpdate(id, newValues);
                } else if (action === 'delete') {
                    if (confirm("Are you sure you want to delete this product")) {
                        this.onDelete(id);
                    }
                } else { // order
                    $("#orderProduct").modal('show');
                    const quantityInput = document.getElementById('amountInput');
                    const orderListener = () => {
                        this.onOrder(id, quantityInput.value);
                    };
                    document.getElementById('orderProductsButton').addEventListener('click', orderListener);
                    $("#orderProduct").on("hidden.bs.modal", () => {
                        document.getElementById('orderProductsButton').removeEventListener('click', orderListener);
                    })
                }
            })

        })
    }

    mapStatesToSort() {
        return Object.keys(this.headerStates).reduce((output, headerName) => {
            if (this.headerStates[headerName] === HEADER_STATES.INITIAL || headerName === 'Actions')
                return output;

            const mappedHeaderName = this.HEADERS_PROPERTIES[headerName];
            output[mappedHeaderName] = this.headerStates[headerName];
            return output;
        }, {})
    }

    getNextState(current) {
        // Surely there is a smarter way
        if (current === HEADER_STATES.INITIAL)
            return HEADER_STATES.ASCENDING;
        else if (current === HEADER_STATES.ASCENDING)
            return HEADER_STATES.DESCENDING;
        else
            return HEADER_STATES.INITIAL
    }

    createActionCell(colStart, rowStart, productId) {
        const orderCell = document.createElement('div');
        orderCell.className = 'cell actions-cell';
        orderCell.style = {
            gridRowStart: rowStart,
            gridColumnStart: colStart,
        }
        orderCell.innerHTML = `
                    <button class="btn btn-primary action-btn" data-id="${productId}" data-action="order">
                        Order
                    </button>
                    <button class="btn btn-primary action-btn" data-id="${productId}" data-action="delete">
                        Delete
                    </button>
                    <button class="btn btn-primary action-btn" data-id="${productId}" data-action="update">
                        Update
                    </button>
                    `
        return orderCell
    }

    /**
     * This is how we trigger rerender on the current items
     * @param items the new items to render
     */
    setItems(items) {
        this.render(items);
    }

    /**
     * Inner render logic
     * @param items items to render
     */
    render(items) {
        this.setHeaders();
        // Replace current content with the headers
        this.container.innerHTML = this.elementHeaders;

        // loop over items and render each individually
        for (let j = 0; j < items.length; j++) {
            // render fields without id
            const { id, ...itemFields } = items[j];
            // fields are the value of the cell, key is the class of the cell (name/description etc etc)
            const dataFields = Object.values(itemFields);
            const dataKeys = Object.keys(itemFields);
            let i;
            for (i = 0; i < dataFields.length; i++) {
                const cell = document.createElement('div');
                cell.className = `cell ${dataKeys[i]}-${id}`;
                cell.style = {
                    gridRowStart: i + 2, // headers are first
                    gridColumnStart: j + 1
                }

                // little hacky but, make the field editable if its from the first three fields (name category description)
                if (i < 3) {
                    cell.classList.add(`editable-${id}`);
                    cell.contentEditable = true;
                    cell.oninput = () => this.didUpdate[id] = true;
                }
                cell.textContent = dataFields[i];
                this.container.append(cell);
            }
            this.container.append(this.createActionCell(j, i, id));
        }

        this.addEventListeners();
    }

}