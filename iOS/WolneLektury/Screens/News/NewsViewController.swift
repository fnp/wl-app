//
//  NewsViewController.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 15/09/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class NewsViewController: ListViewController {
    
    static func instance(dataSource: [Any]?) -> NewsViewController {
        let controller = NewsViewController.instance()
        controller.dataSource = dataSource ?? [BookModel]()
        controller.listViewControllerType = .news
        return controller
    }
    
    override func setupTableView() {
        super.setupTableView()
        tableView.registerNib(name: "NewsTableViewCell")
        tableView.rowHeight = 97
    }
    
    override func getLastObjectAfterParameter() -> String? {
        
        if let last = dataSource.last as? BookModel {
            return last.full_sort_key
        }
        return nil
    }
    
    override func getTableViewCell(tableView: UITableView, indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "NewsTableViewCell", for: indexPath) as! NewsTableViewCell
        cell.setup(newsModel: dataSource[indexPath.row] as! NewsModel)
        return cell
    }
    
    override func didSelectRow(row: Int) {
        if dataSource.count > row {
            navigationController?.pushViewController(NewsDetailsViewController.instance(newsModel: dataSource[row] as! NewsModel) , animated: true)
        }
    }
}
