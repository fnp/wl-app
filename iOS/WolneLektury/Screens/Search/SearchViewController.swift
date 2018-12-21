//
//  SearchViewController.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 13/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import Toast_Swift

enum SearchViewControllerType{
    case search
}

protocol DataLoaderDelegate: class{
    func dataLoaderFetchingServerFinished()
    func dataLoaderFetchingServerSuccess()
    func dataLoaderFetchingServerFailed(showRefreshButton: Bool)
}

class DataLoader: NSObject{
    var dataSource = [BookModel]()
    var loadingMore = false
    var canLoadMore = false
    var currentParams =  FilterBooksParameters()
    weak var delegate: DataLoaderDelegate?
    
    init(delegate: DataLoaderDelegate) {
        self.delegate = delegate
    }
    
    func setNewFilters(filterParams: FilterBooksParameters) {
        let newParams = filterParams
        newParams.search = currentParams.search
        currentParams = newParams
        dataSource = [BookModel]()
    }
    
    func loadData(more: Bool){
        
        let params = currentParams
        if more{
            loadingMore = true
            currentParams.after = dataSource.last?.key
        }
        
        appDelegate.syncManager.filterBooks(params: params) { [weak self] (result) in
            
            guard let strongSelf = self else{
                return
            }
            
            strongSelf.delegate?.dataLoaderFetchingServerFinished()
            strongSelf.loadingMore = false
            
            guard strongSelf.currentParams == params else{
                print("strongSelf.currentParams != cp")
                return
            }
            
            switch result {
            case .success(let model):
                
                let array = model as! [BookModel]
                strongSelf.canLoadMore = array.count == FilterBooksParameters.SEARCH_ITEMS_COUNT
                strongSelf.dataSource.append(contentsOf: array)
                strongSelf.delegate?.dataLoaderFetchingServerSuccess()
                
                
            case .failure(let error):
                if strongSelf.dataSource.count > 0{
                    strongSelf.canLoadMore = false
                    strongSelf.delegate?.dataLoaderFetchingServerFailed(showRefreshButton: false)
                }
                else{
                    strongSelf.delegate?.dataLoaderFetchingServerFailed(showRefreshButton: true)
                }
                break
            }
        }
    }
}

class SearchViewController: MainViewController {

    @IBOutlet weak var filterCloseButton: UIButton!
    @IBOutlet weak var filterContainer: UIView!
    @IBOutlet weak var filterCollectionView: UICollectionView!
    @IBOutlet weak var filterTopConstraint: NSLayoutConstraint!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var footerViewActivityIndicator: UIActivityIndicatorView!
    @IBOutlet weak var noDataLabel: UILabel!
    @IBOutlet weak var refreshDataButton: ActivityIndicatorButton!
    
    var dataLoader: DataLoader!
    var filtersManager : SearchFiltersManager!
    
    var controllerType = SearchViewControllerType.search
    
    lazy var searchBar = UISearchBar(frame: CGRect.zero)
    var currentSearchQuery = ""
    
    class func instance(type: SearchViewControllerType) -> SearchViewController{
        let controller = SearchViewController.instance()
        controller.controllerType = type
        controller.dataLoader = DataLoader(delegate: controller)
        return controller
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        title = "nav_search".localized
        
        filterCloseButton.layer.cornerRadius = 14
        filterCloseButton.tintColor = Constants.Colors.darkGreenBgColor()
        
        noDataLabel.text = "no_results".localized
        noDataLabel.isHidden = true

        refreshDataButton.tintColor = .black
        
        filtersManager = SearchFiltersManager(delegate: self)
        setupTableView()
        setupCollectionView()
        filtersChanged(initially: true, reloadData: true)
        setupSearchBar()
//        searchBar.becomeFirstResponder()
    }
    
    func setupSearchBar() {
        searchBar.placeholder = "search_placeholder".localized
        searchBar.enablesReturnKeyAutomatically = false
        UITextField.appearance(whenContainedInInstancesOf: [type(of: searchBar)]).tintColor = UIColor.gray
        searchBar.delegate = self
        searchBar.translatesAutoresizingMaskIntoConstraints = false

        searchBar.layoutIfNeeded()
        searchBar.sizeToFit()
        searchBar.translatesAutoresizingMaskIntoConstraints = true // make nav bar happy

        navigationItem.titleView = searchBar
//        if #available(iOS 11.0, *) {
////            searchBar.heightAnchor.constraint(equalToConstant: 44).isActive = true
//        }
        let singleTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(self.singleTap(sender:)))
        singleTapGestureRecognizer.numberOfTapsRequired = 1
        singleTapGestureRecognizer.isEnabled = true
        singleTapGestureRecognizer.cancelsTouchesInView = false
        self.view.addGestureRecognizer(singleTapGestureRecognizer)
    }
    
    func setupTableView(){
        tableView.registerNib(name: "BookTableViewCell")
        tableView.separatorStyle = .none
        tableView.delegate = self
        tableView.dataSource = self
        tableView.rowHeight = 137
        var insets = tableView.contentInset
        insets.top = 11
        tableView.contentInset = insets
        footerViewActivityIndicator.color = Constants.Colors.darkGreenBgColor()
        footerViewActivityIndicator.hidesWhenStopped = true
    }
    
    @objc func singleTap(sender: UITapGestureRecognizer) {
        self.searchBar.resignFirstResponder()
    }

    @IBAction func filterCloseButtonAction(_ sender: Any) {
        filtersManager.clearFilters()
    }
    
    @IBAction func refreshDataButtonAction(_ sender: Any) {
        loadData(more: false, fromRefreshControl: false)
    }

    func setupCollectionView(){
        
        filterCollectionView.backgroundColor = UIColor.clear
        filterCollectionView.delegate = filtersManager
        filterCollectionView.dataSource = filtersManager
        filterCollectionView.register(UINib.init(nibName: "SearchFilterCollectionViewCell", bundle: nil), forCellWithReuseIdentifier: "SearchFilterCollectionViewCell")
    }

    func filtersChanged(initially: Bool = false, reloadData: Bool){
        if reloadData{
            filterCollectionView.reloadData()
            
            if filtersManager.numberOfFilters() == 0{
                if filterTopConstraint.constant != 0 && !initially{
                    filterTopConstraint.constant = 0

                    UIView.animate(withDuration:0.5, delay: 0, usingSpringWithDamping: 0.5, initialSpringVelocity: 0.5, options: [], animations: {
                        self.view.layoutIfNeeded()
                    }, completion: {
                        [weak self] (value: Bool) in
                        self?.filterContainer.isHidden = true
                    })
                }
                else{
                    filterTopConstraint.constant = 0
                    filterContainer.isHidden = true
                }
            }
            else{
                filterContainer.isHidden = false
                filterTopConstraint.constant = 44
            }
        }
        
        dataLoader.setNewFilters(filterParams: filtersManager.getParametersForApi())
        
        tableView.reloadData()
        
        loadData(more: false, fromRefreshControl: false)
    }
    
    func loadData(more: Bool, fromRefreshControl: Bool){
        
        refreshDataButton.setIndicatorButtonState(state: .hidden)
        if more && dataLoader.loadingMore{
            return
        }
        
        if !fromRefreshControl{
            footerViewActivityIndicator.startAnimating()
        }
        
        dataLoader.loadData(more: more)
    }
    
    @IBAction func filterButtonAction(_ sender: Any) {
        self.navigationController?.present( WLNavigationController(rootViewController: FilterViewController.instance(delegate: self, selectedKindsArray: filtersManager.selectedKindsArray, selectedEpochsArray: filtersManager.selectedEpochsArray, selectedGenresArray: filtersManager.selectedGenresArray, onlyLectures: filtersManager.onlyLecturesCategory.checked, hasAudiobook: filtersManager.hasAudiobookCategory.checked)) , animated: true, completion: nil)
    }
    
    @objc func getDataForCurrentSearchQuery(){
        if currentSearchQuery.count > 0 {
            dataLoader.currentParams.search = currentSearchQuery
        }
        else{
            dataLoader.currentParams.search = nil
        }
        dataLoader.dataSource = [BookModel]()
        tableView.reloadData()
        loadData(more: false, fromRefreshControl: false)
    }
}

extension SearchViewController: UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int{
        return dataLoader.dataSource.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell{
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "BookTableViewCell", for: indexPath) as! BookTableViewCell
        cell.setup(bookModel: dataLoader.dataSource[indexPath.row])
        return cell
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        if indexPath.row > 0 && indexPath.row == (dataLoader.dataSource.count-1) && dataLoader.canLoadMore && !dataLoader.loadingMore{
            let _ = loadData(more: true, fromRefreshControl: false)
        }
    }
}

extension SearchViewController: UITableViewDelegate{
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        searchBar.resignFirstResponder()
        if dataLoader.dataSource.count > indexPath.row{
            navigationController?.pushViewController(BookDetailsViewController.instance(bookSlug: dataLoader.dataSource[indexPath.row].slug) , animated: true)
        }
    }
}

extension SearchViewController: FilterViewControllerDelegate{
    func filterViewControllerDidSelectItems(kindsArray: [CategoryModel], epochsArray: [CategoryModel], genresArray: [CategoryModel], onlyLectures: Bool, hasAudiobook: Bool, filterChanged: Bool) {
        
        if filterChanged{
            filtersManager.selectedKindsArray = kindsArray
            filtersManager.selectedEpochsArray = epochsArray
            filtersManager.selectedGenresArray = genresArray
            filtersManager.onlyLecturesCategory.checked = onlyLectures
            filtersManager.hasAudiobookCategory.checked = hasAudiobook

            filtersChanged(reloadData: true)
        }
        self.navigationController?.presentedViewController?.dismiss(animated: true, completion: nil)
    }
}

extension SearchViewController: UISearchBarDelegate{
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {// called when 'return' key pressed. return NO to ignore.
        textField.resignFirstResponder()
        return true
    }

    func searchBarTextDidEndEditing(_ searchBar: UISearchBar) {
        var searchQueryCandidate = ""
        if let txt = searchBar.text{
            searchQueryCandidate = txt.trimmed
        }

        if(currentSearchQuery != searchQueryCandidate){
            currentSearchQuery = searchQueryCandidate
            getDataForCurrentSearchQuery()
        }
    }
    
    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        searchBar.resignFirstResponder()
    }
    
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        currentSearchQuery = searchText.trimmed
        
        NSObject.cancelPreviousPerformRequests(withTarget: self, selector: #selector(SearchViewController.getDataForCurrentSearchQuery), object: nil)
        self.perform(#selector(SearchViewController.getDataForCurrentSearchQuery), with: nil, afterDelay: 0.8)
    }
}

extension SearchViewController: SearchFiltersManagerDelegate{
    func searchFiltersManagerFiltersChanged(reloadData: Bool) {
        filtersChanged(reloadData: reloadData)
    }
}

extension SearchViewController: DataLoaderDelegate{
    func dataLoaderFetchingServerFinished() {
        footerViewActivityIndicator.stopAnimating()
    }
    
    func dataLoaderFetchingServerSuccess() {
        noDataLabel.isHidden = dataLoader.dataSource.count > 0
        tableView.reloadData()
    }
    
    func dataLoaderFetchingServerFailed(showRefreshButton: Bool) {
    
        if showRefreshButton{
            refreshDataButton.setIndicatorButtonState(state: .button)
        }
        view.makeToast("book_loading_error".localized, duration: 3.0, position: .bottom)
    }
}
