/**
 * Class handling pagination logic, given a pagination selector,
 * and endpoints for count and getByPage
 */
class Pagination {

    container;
    totalItems;
    itemsPerPage;
    onPageChange;

    currentPage = 0;
    totalPages;

    /**
     *
     * @param containerElement the element into which the pagination will be rendered
     * @param itemsCount the total amount of items
     * @param itemsPerPage the items per page
     * @param onPageChange callback function to execute when page changes
     */
    constructor(containerElement, itemsCount, itemsPerPage, onPageChange) {
        this.container = containerElement;
        this.onPageChange = onPageChange;
        this.totalItems = itemsCount;
        this.itemsPerPage = itemsPerPage;
        this.currentPage = 0;

        this.initPages();
        // Invoke the first page callback
        onPageChange(0);
    }

    setTotalPages(newPages) {
        this.totalItems = newPages;
        this.initPages();
    }

    initPages() {
        this.container.innerHTML = "";

        this.totalPages = Math.ceil(this.totalItems / this.itemsPerPage);
        // If we deleted a single entry from the last page we must go back a page before
        if (this.currentPage >= this.totalPages) {
            this.currentPage = this.totalPages - 1;
            this.onPageChange(this.currentPage);
        }

        const innerContainer = document.createElement('ul');
        innerContainer.classList.add('pagination');


        innerContainer.append(this.createPageItem(
            this.currentPage > 0 ? 'page-item' : 'page-item disabled',
            'First',
            0
        ));

        innerContainer.append(this.createPageItem(
            this.currentPage > 0 ? 'page-item' : 'page-item disabled',
            'Previous',
            this.currentPage - 1
        ));

        for (let i = 0; i < this.totalPages; i++) {
            const pageItem = this.createPageItem(
                i === this.currentPage ? 'page-item active' : 'page-item',
                i + 1,
                i,
            )
            pageItem.classList.add(`page-${i}`);
            innerContainer.append(pageItem);
        }


        innerContainer.append(this.createPageItem(
            this.currentPage < this.totalPages - 1 ? 'page-item' : 'page-item disabled',
            'Next',
            this.currentPage + 1
        ));

        innerContainer.append(this.createPageItem(
            this.currentPage < this.totalPages - 1 ? 'page-item' : 'page-item disabled',
            'Last',
            this.totalPages - 1
        ));

        this.container.append(innerContainer);
    }

    /**
     * Creates page item given the following
     * @param className the class of the page item
     * @param textContent the text to write (page number or first/last/prev/next)
     * @param redirectToPage the page that will be triggered after user clicks on this page item
     * @returns {HTMLDivElement} the page item
     */
    createPageItem(className, textContent, redirectToPage) {
        const pageItem = document.createElement('div');
        const pageLink = document.createElement('a');

        pageItem.className = className;

        pageLink.classList.add('page-link');
        pageLink.textContent = textContent;
        pageLink.addEventListener('click', () => {
            // Remove active class from all pageItem items
            // and add active one to the clicked one
            document.querySelectorAll(`.page-item`).forEach(el => el.classList.remove('active'));
            // every page has class page-{{pageNumber}} so we find the next active page by the next page number
            document.querySelector(`.page-item.page-${redirectToPage}`).classList.add('active');

            this.currentPage = redirectToPage;
            this.initPages();
            this.onPageChange(redirectToPage);
        });


        pageItem.append(pageLink);
        return pageItem
    }

    // All the magic happens this way
    doRefresh() {
        this.initPages();
        this.onPageChange(this.currentPage);
    }
}