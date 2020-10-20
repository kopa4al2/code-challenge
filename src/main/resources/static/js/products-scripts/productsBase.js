const NEW_PRODUCT_URL = "/products/new";
const PRODUCTS_COUNT_URL = '/products/count';
const FETCH_PRODUCTS_URL = '/products/all';
const DELETE_PRODUCTS_URL = '/delete';
const UPDATE_PRODUCTS_URL = '/update';

const ITEMS_PER_PAGE = 10;
let pagination;
let productsTable = new ProductsTable(
    document.querySelector('.products-grid'),
    onSortChange,
    onOrder,
    onDelete,
    onUpdate
);
let currentSort;
let currentPage = 0;

$(() => {
    attachNewProductEventListener();

    addPagination();
})

/**
 * Init the pagination object with the count of objects from the DB
 */
function addPagination() {
    fetch(PRODUCTS_COUNT_URL)
        .then(res => res.json())
        .then(({ body }) => {
            pagination = new Pagination(
                document.querySelector('.products-pagination'),
                body,
                ITEMS_PER_PAGE,
                handlePageChange,
            )
        })
}

/**
 * Fetches the new products count and updates the pagination object
 */
function updatePages() {
    fetch(PRODUCTS_COUNT_URL)
        .then(res => res.json())
        .then(({ body }) => {
          pagination.setTotalPages(body);
        })
}

/**
 * Changed some sort direction/field
 * @param sortObject the new object with sort fields and directions
 */
function onSortChange(sortObject) {
    currentSort = sortObject;
    fetchProducts();
}

/**
 * Client ordered some qunatity of product with id = productId
 * @param productId the id of the product
 * @param quantity the quantity ordered
 */
function onOrder(productId, quantity) {
    fetch(`/product/${productId}/order/${quantity || 0}/`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
    })
        .then(res => res.json().then(json => res.ok ? json : Promise.reject(json)))
        .then(() => {
            window.showNotification(NOTIFICATION_TYPES.SUCCESS, "Successfully ordered product")
            pagination.doRefresh();
        })
        .catch(error => {
            window.showNotification(NOTIFICATION_TYPES.ERROR, error.body || error.message);
        })
}

/**
 * Client wants to delete product
 * @param productId the id of the product
 */
function onDelete(productId) {
    fetch(`${DELETE_PRODUCTS_URL}/${productId}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        },
    })
        .then(res => res.json().then(json => res.ok ? json : Promise.reject(json)))
        .then(() => {
            window.showNotification(NOTIFICATION_TYPES.SUCCESS, "Successfully deleted product");
            updatePages();
            pagination.doRefresh();
        })
        .catch(error => {
            window.showNotification(NOTIFICATION_TYPES.ERROR, error.body || error.message);
        })
}

/**
 * Client wants to update product
 * @param productId the id of the product
 * @param newValues the object with keys - fields to update and values - new value of the field
 */
function onUpdate(productId, newValues) {
    fetch(`${UPDATE_PRODUCTS_URL}/${productId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(newValues)
    })
        .then(res => res.json().then(json => res.ok ? json : Promise.reject(json)))
        .then(() => {
            window.showNotification(NOTIFICATION_TYPES.SUCCESS, "Successfully deleted product")
            pagination.doRefresh();
        })
        .catch(error => {
            window.showNotification(NOTIFICATION_TYPES.ERROR, error.body || error.message);
        })
}

/**
 * Callback invoked when user clicks a page
 * @param newPage the next page number
 */
function handlePageChange(newPage) {
    currentPage = newPage;
    fetchProducts();
}

/**
 * If we dont have sort object, hence we only need pagination invoke GET request on one endpoint
 * else invoke post request with body being page and sort
 */
function fetchProducts() {
    if (currentSort === null ||
        typeof currentSort !== 'object' ||
        Object.keys(currentSort).length === 0) {
        fetch(FETCH_PRODUCTS_URL + `?offset=${currentPage}&limit=${ITEMS_PER_PAGE}`)
            .then(res => res.json().then(json => res.ok ? json : Promise.reject(json)))
            .then(parsed => {
                productsTable.setItems(parsed.body);
            })
            .catch(error => {
                window.showNotification(NOTIFICATION_TYPES.ERROR, error.body || error.message);
            })
    } else {
        fetch(FETCH_PRODUCTS_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                sortedProperties: currentSort || {},
                pageNumber: currentPage,
                itemsPerPage: ITEMS_PER_PAGE,
            })
        })
            .then(res => res.json().then(json => res.ok ? json : Promise.reject(json)))
            .then(parsed => {
                productsTable.setItems(parsed.body);
            })
            .catch((error) => {
                window.showNotification(NOTIFICATION_TYPES.ERROR, error.body || error.message);
            })
    }
}

/**
 * Add new product logic (from binding of event listeners,
 * through validating the form to submitting the request and handling response)
 */
function attachNewProductEventListener() {
    document.getElementById('addProductBtn').addEventListener('click', e => {
        e.preventDefault();
        e.stopPropagation();

        const submitData = {};
        let hasErrors = false;
        document.querySelectorAll('#newProductForm > .form-group > .form-control')
            .forEach(formControl => {
                const { value, required, name } = formControl;
                if (required && !value) {
                    showError(formControl);
                    hasErrors = true;
                } else {
                    submitData[name] = value;
                }
            });

        // Submit form if no errors
        if (!hasErrors) {
            fetch(NEW_PRODUCT_URL, {
                method: 'POST',
                mode: 'cors',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(submitData)
            })
                .then(res => res.json().then(json => res.ok ? json : Promise.reject(json)))
                .then(({ body }) => {
                    window.showNotification(NOTIFICATION_TYPES.SUCCESS,
                        `successfully inserted ${body.name} in ${body.category}`);

                    updatePages();
                    pagination.doRefresh();
                })
                .catch(error => {
                    window.showNotification(NOTIFICATION_TYPES.ERROR, error.body || error.message);
                })
        }
    })
}

function showError(inputElement) {
    // Remove the error class when the user starts writing
    $(inputElement).on('keyup', el => {
        $(el.target).removeClass('error')
        $(el.target).tooltip('disable');
    });

    $(inputElement).toggleClass('error');
    $(inputElement).tooltip({ title: `This field is required` });
    $(inputElement).tooltip('enable');
    $(inputElement).tooltip('show');
}