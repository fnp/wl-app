//
//  ListViewController.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 15/09/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

enum ListViewControllerType{
    case newest
    case recommended
    case reading_now
    case audiobooks
    case news
    case favourites
    case completed

    var title: String{
        return "\(self)".localized
    }
    
    var emptyTitle: String{
        return "\(self)_empty_list".localized
    }
        
    var canLoadMore: Bool{
        switch self {
        case .reading_now, .audiobooks, .news, .favourites, .completed:
            return true
        default:
            return false
        }
    }
    
    var menuItem: MenuItem? {
        switch self {
        case .reading_now:
            return .reading
        case .audiobooks:
            return .audiobooks
        case .news:
            return .news
        case .favourites:
            return .favourites
        case .completed:
            return .completed
        default:
            return nil
        }
    }
}

class ListViewController: MainViewController  {
    
    @IBOutlet weak var footerViewActivityIndicator: UIActivityIndicatorView!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var noDataLabel: UILabel!
    @IBOutlet weak var refreshDataButton: ActivityIndicatorButton!
    
    var currentParams =  FilterBooksParameters()
    var canLoadMore = false
    var loadingMore = false
    var dataSource: [Any]!
    var listViewControllerType: ListViewControllerType!
    
    override func name() -> String {
        return "\(listViewControllerType!)List"
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if listViewControllerType! == .audiobooks {
            navigationItem.rightBarButtonItem = UIBarButtonItem(image: #imageLiteral(resourceName: "navbar_search"), style: .plain, target: self, action: #selector(presentSearch))
        }
        
        title = listViewControllerType.title
        
        noDataLabel.text = listViewControllerType.emptyTitle
        noDataLabel.isHidden = true
        
        refreshDataButton.tintColor = .black
        setupTableView()
        
        refreshDataButton.setIndicatorButtonState(state: .hidden)
        if dataSource.count == 0{
            loadData(more: false)
        }
    }
    
    func setupTableView(){
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
    
    @objc func presentSearch() {
        appDelegate.mainNavigator.presentSearch()
    }

    @IBAction func refreshDataButtonAction(_ sender: Any) {
        loadData(more: false)
    }
    
    func getLastObjectAfterParameter() -> String? {
        assertionFailure("This method should be overriden")
        return nil
    }
    
    func getTableViewCell(tableView: UITableView, indexPath: IndexPath) -> UITableViewCell {
        assertionFailure("This method should be overriden")
        return UITableViewCell()
    }
    
    func didSelectRow(row: Int) {
        assertionFailure("This method should be overriden")
    }
    
    func loadData(more: Bool){
        
        if more{
            loadingMore = true
            currentParams.after = getLastObjectAfterParameter()
        }
        
        noDataLabel.isHidden = true
        footerViewActivityIndicator.startAnimating()

        syncManager.getDataForListType(listViewControllerType: listViewControllerType, params: listViewControllerType.canLoadMore ? currentParams : nil) { [weak self] (result) in
            
            guard let strongSelf = self else{
                return
            }
            
            strongSelf.loadingMore = false
            strongSelf.footerViewActivityIndicator.stopAnimating()

            switch result {
            case .success(let model):
                
                let array = model as! [Any]
                strongSelf.canLoadMore =
                    strongSelf.listViewControllerType.canLoadMore
                    && array.count == FilterBooksParameters.SEARCH_ITEMS_COUNT
                strongSelf.dataSource.append(contentsOf: array)
                strongSelf.refreshDataButton.setIndicatorButtonState(state: .hidden)
                
                strongSelf.noDataLabel.isHidden = strongSelf.dataSource.count > 0
                strongSelf.tableView.reloadData()
                
            case .failure/*(let error)*/:
                if strongSelf.dataSource.count > 0{
                    strongSelf.canLoadMore = false
                    strongSelf.refreshDataButton.setIndicatorButtonState(state: .hidden)
                }
                else{
                    strongSelf.refreshDataButton.setIndicatorButtonState(state: .button)
                }
            }
        }
    }
}

extension ListViewController: UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int{
        return dataSource.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell{
        
        return getTableViewCell(tableView: tableView, indexPath: indexPath)
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        if indexPath.row > 0 && indexPath.row == (dataSource.count-1) && canLoadMore && !loadingMore{
            let _ = loadData(more: true)
        }
    }
}

extension ListViewController: UITableViewDelegate{
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        didSelectRow(row: indexPath.row)
    }
}
