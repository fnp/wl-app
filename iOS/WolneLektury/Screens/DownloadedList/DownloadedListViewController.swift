//
//  DownloadedListViewController.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 12/09/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class DownloadedListViewController: MainViewController {

    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var noDataLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        title = "nav_downloaded".localized
        
        noDataLabel.text = "downloaded_empty_list".localized
        noDataLabel.isHidden = true
        setupTableView()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        reloaData()
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

    }
    
    func reloaData() {
        DatabaseManager.shared.refresh()
        tableView.reloadData()
        noDataLabel.isHidden = (DatabaseManager.shared.rlmApplication?.downloadedBooks.count ?? 0) > 0
    }
}

extension DownloadedListViewController: UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int{
        return DatabaseManager.shared.rlmApplication?.downloadedBooks.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell{
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "BookTableViewCell", for: indexPath) as! BookTableViewCell
        
        if let downloadedBooks = DatabaseManager.shared.rlmApplication?.downloadedBooks, downloadedBooks.count > indexPath.row {
            cell.setup(bookDetailsModel: downloadedBooks[indexPath.row], delegate: self, indexPath: indexPath)
        } 
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {

    }
}

extension DownloadedListViewController: UITableViewDelegate{
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        guard let dataSource = DatabaseManager.shared.rlmApplication?.downloadedBooks else {
            return
        }

        if dataSource.count > indexPath.row{
            navigationController?.pushViewController(BookDetailsViewController.instance(bookSlug: dataSource[indexPath.row].slug) , animated: true)
        }
    }
}

extension DownloadedListViewController: BookTableViewCellDelegate{
    func bookTableViewCellDelegateDidTapTrashButton(bookSlug: String, indexPath: IndexPath?) {
        
        if let slug = PlayerController.shared.currentBookDetails?.slug, slug == bookSlug {
            PlayerController.shared.stopAndClear()
        }
        if let index = DatabaseManager.shared.rlmApplication?.downloadedBooks.index(where: {$0.slug == bookSlug}), DatabaseManager.shared.removeBookFromDownloaded(bookSlug: bookSlug){
            tableView.deleteRows(at: [IndexPath(row: index, section: 0)], with: .automatic)
            presentToast(message: "book_deleted_message".localized)
        }
        else{
            reloaData()
        }        
    }
}

