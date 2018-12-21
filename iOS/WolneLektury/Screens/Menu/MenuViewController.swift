//
//  MenuViewController.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 29/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import OAuthSwift
import SafariServices

class MenuViewController: UIViewController {

    @IBOutlet weak var tableView: UITableView!
    
    var cellsArray: [WLTableViewCell]!
    var loggedIn = false
    
    @IBOutlet weak var bottomView: UIView!
    @IBOutlet weak var bottomViewBgView: UIView!

    @IBOutlet weak var guestBottomView: UIView!
    @IBOutlet weak var loginButton: UIButton!
    
    @IBOutlet weak var userBottomView: UIView!
    @IBOutlet weak var loggedInLabel: UILabel!
    @IBOutlet weak var logoutButton: UIButton!
    
    @IBOutlet weak var bottomViewHeightConstraint: NSLayoutConstraint!
    
    var selectedRow: IndexPath?
    var wlRow: Int = 0
    var firstAppear = true
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.separatorStyle = .none

        var contentInset = tableView.contentInset
        contentInset.top = 10
        tableView.contentInset = contentInset
        
        loginButton.text = "menu_login".localized
        logoutButton.text = "sign_out".localized
        loginButton.layer.cornerRadius = 15
        loginButton.layer.borderColor = UIColor.white.cgColor
        loginButton.layer.borderWidth = 1
        logoutButton.layer.cornerRadius = 15
        logoutButton.layer.borderColor = UIColor.white.cgColor
        logoutButton.layer.borderWidth = 1

        bottomView.backgroundColor = Constants.Colors.menuTintColor()
        bottomViewBgView.backgroundColor = Constants.Colors.menuTintColor()

        
        setup()
        
        appDelegate.sideMenuNavigationController = self.navigationController
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        if firstAppear{
            tableView.selectRow(at: IndexPath(row: wlRow, section: 0), animated: false, scrollPosition: .none)
            firstAppear = false
        }
    }
    func setup() {
        if syncManager.isLoggedIn(){
            cellsArray = [WLTableViewCell]()
            
            if DatabaseManager.shared.isUserPremium() == false && Constants.donateEnabled{
                cellsArray.append(MenuSupportUsTableViewCell.instance(delegate: self))
            }
            
            if selectedRow == nil {
                selectedRow = IndexPath(row: cellsArray.count, section: 0)
                tableView.selectRow(at: selectedRow, animated: false, scrollPosition: .none)
            }

            cellsArray.append(MenuTableViewCell.instance(menuItem: .wolne_lektury))
            wlRow = cellsArray.count - 1
            cellsArray.append(MenuLineTableViewCell.instance())
            if Constants.donateEnabled {
                cellsArray.append(MenuTableViewCell.instance(menuItem: .premium))
            }
            cellsArray.append(MenuTableViewCell.instance(menuItem: .catalog))
            cellsArray.append(MenuTableViewCell.instance(menuItem: .audiobooks))
            cellsArray.append(MenuTableViewCell.instance(menuItem: .reading))
            cellsArray.append(MenuTableViewCell.instance(menuItem: .favourites))
            cellsArray.append(MenuTableViewCell.instance(menuItem: .completed))
            cellsArray.append(MenuLineTableViewCell.instance())
            cellsArray.append(MenuTableViewCell.instance(menuItem: .downloaded))
            cellsArray.append(MenuLineTableViewCell.instance())
            cellsArray.append(MenuTableViewCell.instance(menuItem: .news))
            cellsArray.append(MenuTableViewCell.instance(menuItem: .settings))
            cellsArray.append(MenuTableViewCell.instance(menuItem: .about))
            
        }
        else{
            cellsArray = [WLTableViewCell]()
            
            if Constants.donateEnabled {
                cellsArray.append(MenuSupportUsTableViewCell.instance(delegate: self))
            }

            if selectedRow == nil {
                selectedRow = IndexPath(row: cellsArray.count, section: 0)
                tableView.selectRow(at: selectedRow, animated: false, scrollPosition: .none)
            }

            cellsArray.append(MenuTableViewCell.instance(menuItem: .wolne_lektury))
            wlRow = cellsArray.count - 1
            cellsArray.append(MenuLineTableViewCell.instance())
            if Constants.donateEnabled {
                cellsArray.append(MenuTableViewCell.instance(menuItem: .premium))
            }
            
            cellsArray.append(MenuTableViewCell.instance(menuItem: .catalog))
            cellsArray.append(MenuTableViewCell.instance(menuItem: .audiobooks))
            cellsArray.append(MenuLineTableViewCell.instance())
            cellsArray.append(MenuTableViewCell.instance(menuItem: .downloaded))
            cellsArray.append(MenuLineTableViewCell.instance())
            cellsArray.append(MenuTableViewCell.instance(menuItem: .news))
            cellsArray.append(MenuTableViewCell.instance(menuItem: .settings))
            cellsArray.append(MenuTableViewCell.instance(menuItem: .about))
        }
        tableView.reloadData()
        
        refreshBottomView()
    }
    
    func selectItem(menuItem: MenuItem){
        var index = 0
        for cell in cellsArray {
            if let cell = cell as? MenuTableViewCell, cell.menuItem == menuItem {
                tableView.selectRow(at: IndexPath(row: index, section: 0), animated: false, scrollPosition: .none)
                return
            }
            index += 1
        }
    }
    
    func refreshBottomView() {
        
        userBottomView.isHidden = true
        guestBottomView.isHidden = true
        
        if syncManager.isLoggedIn(){
            
            var titleText = NSMutableAttributedString(string: "")
            
            if let user = DatabaseManager.shared.rlmApplication?.user {
                
                titleText = NSMutableAttributedString(attributedString: NSAttributedString(string: "logged_as".localized + "\n", font: UIFont.systemFont(ofSize: 10)))
                titleText.append(NSAttributedString(string: user.username, font: UIFont.systemFont(ofSize: 12)))
                loggedInLabel.attributedText = titleText
            }

            userBottomView.isHidden = false
            bottomViewHeightConstraint.constant = userBottomView.frame.size.height
        }
        else{
            guestBottomView.isHidden = false
            bottomViewHeightConstraint.constant = guestBottomView.frame.size.height
        }
    }
    
    func getPreview() {
        appDelegate.showWindowHud()
        syncManager.getPreview { [weak self] (result) in
            
            guard let strongSelf = self else { return }
            strongSelf.appDelegate.hideWindowHud()
            switch result {
            case .success(let model):
                let array = model as! [BookModel]
                if array.count > 0 {
                    let book = array[0]
                    
                    strongSelf.appDelegate.mainNavigator.presentPremiumBook(bookSlug: book.slug)
                }
                else {
                    strongSelf.presentNoPremiereAlert()
                }
                
            case .failure/*(let error)*/:
                strongSelf.presentToast(message: "fetching_premium_failed".localized)
            }
        }
    }
    
    func presentNoPremiereAlert() {
    
        let message = syncManager.isLoggedIn() ? "no_prapremiere_message_logged".localized : "no_prapremiere_message".localized
        
        let alertController = UIAlertController(
            title: "no_prapremiere_title".localized,
            message: message,
            preferredStyle: UIAlertControllerStyle.alert
        )
        
        if syncManager.isLoggedIn() {
            let action = UIAlertAction(title: "OK".localized, style: UIAlertActionStyle.cancel, handler: nil)
            alertController.addAction(action)
        }
        else{
            let cancelAction = UIAlertAction(title: "no_thanks".localized, style: UIAlertActionStyle.cancel, handler: nil)
            alertController.addAction(cancelAction)
            
            let okAction = UIAlertAction(title: "become_a_friend".localized, style: UIAlertActionStyle.default) { [weak self]
                (result : UIAlertAction) -> Void in
                self?.onBecomeAFriendClick()
            }
            alertController.addAction(okAction)
        }
        present(alertController, animated: true, completion: nil)
    }
    
    func onBecomeAFriendClick() {
        if syncManager.isLoggedIn() {
            showPremiumForm()
        }
        else {
            showLoginFirst()
        }
    }
    
    func showPremiumForm() {
        showPayPalForm()
    }
    
    func showPayPalForm() {
        
        if syncManager.isLoggedIn() {
            appDelegate.mainNavigator.presentPayPalForm()
        }
        else {
            presentToast(message: "login_first".localized)
        }
    }
    
    func showLoginFirst() {
        
        let alertController = UIAlertController(
            title: "login".localized,
            message: "login_first".localized,
            preferredStyle: UIAlertControllerStyle.alert
        )
        
        let cancelAction = UIAlertAction(title: "no_thanks".localized, style: UIAlertActionStyle.cancel, handler: nil)
        alertController.addAction(cancelAction)
        
        let loginAction = UIAlertAction(title: "login".localized, style: UIAlertActionStyle.default) { [weak self]
            (result : UIAlertAction) -> Void in
            self?.onLoginClicked()
        }
        alertController.addAction(loginAction)

        self.present(alertController, animated: true, completion: nil)
    }
    
    func onLoginClicked() {
        appDelegate.login(fromViewController: self)
    }

    deinit {
        print("deinit")
        appDelegate.sideMenuNavigationController = nil
    }
    
    @IBAction func logoutButtonAction(_ sender: Any) {
        syncManager.logout()
        appDelegate.mainNavigator.reset()
    }
    
    @IBAction func loginButtonAction(_ sender: Any) {
        onLoginClicked()
    }
}

extension MenuViewController: UITableViewDataSource{
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return cellsArray[indexPath.row]
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return cellsArray.count
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return cellsArray[indexPath.row].getHeight()
    }
}

extension MenuViewController: UITableViewDelegate{
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        if let cell = cellsArray[indexPath.row] as? MenuTableViewCell{
            switch cell.menuItem{
            case .wolne_lektury:
                appDelegate.mainNavigator.presentLibrary(dismissSideMenu: true)
            case .premium:
                getPreview()
                if let selectedRow = selectedRow{
                    tableView.selectRow(at: selectedRow, animated: false, scrollPosition: .none)
                }
                return
            case .audiobooks:
                appDelegate.mainNavigator.presentBookList(listViewControllerType: .audiobooks)
            case .downloaded:
                appDelegate.mainNavigator.presentDownloaded()
            case .catalog:
                appDelegate.mainNavigator.presentSearch()
            case .news:
                appDelegate.mainNavigator.presentNews()
            case .settings:
                appDelegate.mainNavigator.presentSettings()
            case .about:
                appDelegate.mainNavigator.presentAbout()
            case .reading:
                appDelegate.mainNavigator.presentBookList(listViewControllerType: .reading_now)
            case .favourites:
                appDelegate.mainNavigator.presentBookList(listViewControllerType: .favourites)
            case .completed:
                appDelegate.mainNavigator.presentBookList(listViewControllerType: .completed)
            default:
                break
            }
            selectedRow = indexPath
        }
        else if cellsArray[indexPath.row] is MenuSupportUsTableViewCell{
            selectedRow = indexPath
        }
        else {
            if let selectedRow = selectedRow{
                tableView.selectRow(at: selectedRow, animated: false, scrollPosition: .none)
            }
        }
    }
}

extension MenuViewController: MenuSupportUsTableViewCellDelegate{
    func menuSupportUsButtonTapped(){
        appDelegate.mainNavigator.presentSupportUs()
    }
}

