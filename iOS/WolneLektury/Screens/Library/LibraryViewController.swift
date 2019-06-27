//
//  LibraryViewController.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 13/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit


class LibraryViewController: MainViewController {
    enum LibraryCellState {
        case not_loaded
        case loading
        case loaded
    }
    
    @IBOutlet weak var tableView: UITableView!

    var cellsArray = [WLTableViewCell]()
    
    let earlyAccessCell = LibraryEarlyAccessTableViewCell.instance()
    let newestCell = LibraryCollectionTableViewCell.instance(libraryCollectionType: .newest)
    let recommendedCell = LibraryCollectionTableViewCell.instance(libraryCollectionType: .recommended)
    let readingNowCell = LibraryCollectionTableViewCell.instance(libraryCollectionType: .reading_now)

    var earlyAccessCellState: LibraryCellState = .not_loaded
    var newestCellState: LibraryCellState = .not_loaded
    var recommendedCellState: LibraryCellState = .not_loaded
    var readingCellState: LibraryCellState = .not_loaded
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        title = "nav_library".localized
        navigationItem.rightBarButtonItem = UIBarButtonItem(image: #imageLiteral(resourceName: "navbar_search"), style: .plain, target: self, action: #selector(presentSearch))
        
        tableView.registerNib(name: "LibraryEarlyAccessTableViewCell")
        tableView.registerNib(name: "LibraryCollectionTableViewCell")
        tableView.registerNib(name: "BecomeFriendTableViewCell")
        tableView.separatorStyle = .none
        
        if DatabaseManager.shared.isUserPremium() == false /* && Constants.donateEnabled*/{
            cellsArray.append(BecomeFriendTableViewCell.instance(delegate: self))
        }
        
        if Constants.donateEnabled {
            earlyAccessCell.delegate = self
            cellsArray.append(earlyAccessCell)
        }
        
        if syncManager.isLoggedIn() {
            readingNowCell.delegate = self
            cellsArray.append(readingNowCell)
        }
        
        newestCell.delegate = self
        cellsArray.append(newestCell)

        recommendedCell.delegate = self
        cellsArray.append(recommendedCell)

    }
    
    @objc func presentSearch() {
        appDelegate.mainNavigator.presentSearch()
    }
    
    func getDataFor(libraryCollectionType: LibraryCollectionType) {

        var libraryCellState = LibraryCellState.not_loaded
        switch libraryCollectionType {
        case .reading_now:
            libraryCellState = readingCellState
        case .newest:
            libraryCellState = newestCellState
        case .recommended:
            libraryCellState = recommendedCellState
        }
        
        guard libraryCellState == .not_loaded else {
            return
        }
        
        switch libraryCollectionType {
        case .reading_now:
            readingCellState = .loading
            readingNowCell.setup(state: .loading, dataSource: nil)
        case .newest:
            newestCellState = .loading
            newestCell.setup(state: .loading, dataSource: nil)
        case .recommended:
            recommendedCellState = .loading
            recommendedCell.setup(state: .loading, dataSource: nil)
        }
        
        syncManager.getDataForLibrary(libraryCollectionType: libraryCollectionType) { [weak self] (result) in
           
            guard let strongSelf = self else { return }
            
            switch result {
            case .success(let model):
                
                let array = model as! [BookModel]
                
                switch libraryCollectionType {
                case .reading_now:
                    strongSelf.readingCellState = .loaded
                    strongSelf.readingNowCell.setup(state: .hidden, dataSource: array)
                case .newest:
                    strongSelf.newestCellState = .loaded
                    strongSelf.newestCell.setup(state: .hidden, dataSource: array)
                    strongSelf.tableView.reloadData()
                    
                case .recommended:
                    strongSelf.recommendedCellState = .loaded
                    strongSelf.recommendedCell.setup(state: .hidden, dataSource: array)
                }
                
            case .failure(let error):
                switch libraryCollectionType {
                case .reading_now:
                    strongSelf.readingCellState = .not_loaded
                    strongSelf.readingNowCell.setup(state: .button, dataSource: nil)
                case .newest:
                    strongSelf.newestCellState = .not_loaded
                    strongSelf.newestCell.setup(state: .button, dataSource: nil)
                case .recommended:
                    strongSelf.recommendedCellState = .not_loaded
                    strongSelf.recommendedCell.setup(state: .button, dataSource: nil)
                }
            }
        }
    }
    
    func getPreview() {
        guard earlyAccessCellState == .not_loaded else {
            return
        }

        if Constants.donateEnabled {
            earlyAccessCellState = .loaded
            earlyAccessCell.setup(state: .hidden, bookDetailsModel: nil)
        }
        
        
        
        earlyAccessCellState = .loading
        earlyAccessCell.setup(state: .loading, bookDetailsModel: nil)
        
        syncManager.getPreview { [weak self] (result) in
            
            guard let strongSelf = self else { return }
            
            switch result {
            case .success(let model):
                let array = model as! [BookDetailsModel]
                strongSelf.earlyAccessCellState = .loaded
                strongSelf.earlyAccessCell.setup(state: .hidden, bookDetailsModel: array.count > 0 ? array[0] : nil)
            case .failure(let error):
                strongSelf.earlyAccessCellState = .not_loaded
                strongSelf.earlyAccessCell.setup(state: .button, bookDetailsModel: nil)
            }
            strongSelf.tableView.reloadData()
        }
    }
}

extension LibraryViewController: UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return cellsArray.count
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        
        return cellsArray[indexPath.row].getHeight()
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        let cell = cellsArray[indexPath.row]
        if cell == earlyAccessCell {
            getPreview()
        }
        else if cell == newestCell {
            getDataFor(libraryCollectionType: .newest)
        }
        else if cell == recommendedCell {
            getDataFor(libraryCollectionType: .recommended)
        }
        else if cell == readingNowCell {
            getDataFor(libraryCollectionType: .reading_now)
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

        return cellsArray[indexPath.row]
    }
}

extension LibraryViewController: UITableViewDelegate {
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if let cell = cellsArray[indexPath.row] as? LibraryEarlyAccessTableViewCell, earlyAccessCellState == .loaded {
            if let book = cell.book {
                let controller = BookDetailsViewController.instance(bookSlug: book.slug, isBookPremium: true)
                navigationController?.pushViewController(controller, animated: true)
            }
            else {
                // no action
            }
        }
    }
}

extension LibraryViewController: LibraryEarlyAccessTableViewCellDelegate {
    func libraryEarlyAccessTableViewCellRefreshButtonTapped() {
        getPreview()
    }
}

extension LibraryViewController: LibraryCollectionTableViewCellDelegate {
    func libraryCollectionTableViewCellDelegateRefreshButtonTapped(collectionViewType: LibraryCollectionType) {
        getDataFor(libraryCollectionType: collectionViewType)
    }
    
    func libraryCollectionTableViewCellDelegateShowAllButtonTapped(collectionViewType: LibraryCollectionType) {
        
        var bookListViewControllerType: ListViewControllerType?
        var dataSource: [BookModel]?
        switch collectionViewType {
        case .newest:
            bookListViewControllerType = .newest
            dataSource = newestCell.dataSource
        case .reading_now:
            bookListViewControllerType = .reading_now
            dataSource = readingNowCell.dataSource
        case .recommended:
            bookListViewControllerType = .recommended
            dataSource = recommendedCell.dataSource
        }
    
        if let controllerType = bookListViewControllerType {
            let controller = BookListViewController.instance(listViewControllerType: controllerType, dataSource: dataSource)
            controller.leftBarButtonItemShouldOpenMenu = false
            navigationController?.pushViewController(controller, animated: true)
        }
    }
    
    func libraryCollectionTableViewCellDelegateDidSelect(bookModel: BookModel) {
        let controller = BookDetailsViewController.instance(bookSlug: bookModel.slug)
        navigationController?.pushViewController(controller, animated: true)
    }
}

extension LibraryViewController: BecomeFriendTableViewCellDelegate {
    func becomeFriendTableViewCellTapped() {
        getPreview()
//        appDelegate.mainNavigator.presentSupportUs()
    }
}

