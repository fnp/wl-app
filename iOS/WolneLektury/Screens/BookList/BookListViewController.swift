//
//  BookListViewController.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/08/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class BookListViewController: ListViewController {
    
    static func instance(listViewControllerType: ListViewControllerType, dataSource: [Any]?) -> BookListViewController {
        let controller = BookListViewController.instance()
        controller.dataSource = dataSource ?? [BookModel]()
        controller.listViewControllerType = listViewControllerType
        return controller
    }
    
    override func setupTableView() {
        tableView.registerNib(name: "BookTableViewCell")
        super.setupTableView()
    }

    override func getLastObjectAfterParameter() -> String? {

        if let last = dataSource.last as? BookModel {
            return last.full_sort_key
        }
        return nil
    }
    
    override func getTableViewCell(tableView: UITableView, indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "BookTableViewCell", for: indexPath) as! BookTableViewCell
        cell.setup(bookModel: dataSource[indexPath.row] as! BookModel)
        return cell
    }
    
    override func didSelectRow(row: Int) {
        if dataSource.count > row {
            navigationController?.pushViewController(BookDetailsViewController.instance(bookSlug: (dataSource[row] as! BookModel).slug) , animated: true)
        }
    }
}
