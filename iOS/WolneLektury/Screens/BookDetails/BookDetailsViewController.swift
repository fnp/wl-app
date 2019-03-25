//
//  BookDetailsViewController.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 19/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import MZDownloadManager
import FolioReaderKit
import MatomoTracker

class BookDetailsViewController: WLViewController {
    
    var bookDetailsModel: BookDetailsModel?
    private var bookSlug: String!
    private var isFavourite = false
    private var isBookPremium: Bool = false
    @IBOutlet weak var headerView: UIView!
    @IBOutlet weak var refreshButton: ActivityIndicatorButton!
    @IBOutlet weak var backButton: UIButton!
    @IBOutlet weak var tableView: UITableView!
    
    @IBOutlet weak var favouriteButton: UIButton!
    @IBOutlet weak var shareButton: UIButton!
    @IBOutlet weak var buttonsContainer: UIView!
    @IBOutlet weak var headerHeightConstraint: NSLayoutConstraint!
    @IBOutlet weak var buttonsContainerWidthConstraint: NSLayoutConstraint!

    @IBOutlet weak var becomeFriendView: UIView!
    @IBOutlet weak var becomeFriendLabel: UILabel!
    @IBOutlet weak var becomeFriendButton: UIButton!
    @IBOutlet weak var becomeFriendStarImageView: UIImageView!
    @IBOutlet weak var headerLabel: UILabel!
    
    var topColor = UIColor(red:0.91, green:0.31, blue:0.20, alpha:1.00)
    var headerProgress: CGFloat = 100

    var cellsArray = [WLTableViewCell]()
    var readCell: BookDetailsButtonTableViewCell?
    var audiobookCell: BookDetailsButtonTableViewCell?
    var readingState: ReadingStateModel.ReadingState = .unknown
    
    @IBAction func refreshButtonAction(_ sender: Any) {
        refreshData()
    }
    
    @IBAction func becomeFriendButtonAction(_ sender: Any) {
        onBecomeFriendButtonTapped()
    }
    
    @IBAction func backButtonAction(_ sender: Any) {
        navigationController?.popViewController(animated: true)
    }
    
    @IBAction func favouriteButtonAction(_ sender: Any) {
        setFavourite(favourite: !isFavourite)
    }

    @IBAction func sharebuttonAction(_ sender: UIButton) {
        
        if let url = bookDetailsModel?.url {
            self.share(string: url, button: sender)
        }
    }
    
    static func instance(bookSlug: String, isBookPremium: Bool = false) -> BookDetailsViewController{
        let controller = BookDetailsViewController.instance()
        controller.bookSlug = bookSlug
        controller.isBookPremium = isBookPremium
        return controller
    }

    var bigHeaderHeight: CGFloat = 200
    var smallHeaderHeight: CGFloat = 200
    
    open override var preferredStatusBarStyle : UIStatusBarStyle {
        return .lightContent
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        
        headerLabel.text = ""
        tableView.isHidden = true
        buttonsContainer.isHidden = true
        becomeFriendStarImageView.tintColor = UIColor.white
        becomeFriendButton.layer.cornerRadius = 18
        refreshButton.tintColor = UIColor.white

        bigHeaderHeight = 190 + UIApplication.shared.statusBarFrame.size.height
        smallHeaderHeight = 44 + UIApplication.shared.statusBarFrame.size.height
        headerHeightConstraint.constant = bigHeaderHeight
        
        headerView.backgroundColor = Constants.Colors.navbarBgColor()
        tableView.rowHeight = UITableViewAutomaticDimension
        tableView.estimatedRowHeight = 200
        tableView.separatorStyle = .none
        tableView.registerNib(name: "BookDetailsHeaderTableViewCell")
        tableView.registerNib(name: "BookDetailsInfoTableViewCell")
        refreshData()
        
        if syncManager.isLoggedIn() == false{
            favouriteButton.isHidden = true
            buttonsContainerWidthConstraint.constant = 42
        }
        
        let tintColor = UIColor(red:0.91, green:0.31, blue:0.20, alpha:1.00)
        favouriteButton.tintColor = tintColor
        shareButton.tintColor = tintColor
        favouriteButton.layer.cornerRadius = 21
        shareButton.layer.cornerRadius = 21
        let buttonBorderColor = UIColor(red:0.96, green:0.96, blue:0.96, alpha:1.00)
        favouriteButton.layer.borderColor = buttonBorderColor.cgColor
        shareButton.layer.borderColor = buttonBorderColor.cgColor
        favouriteButton.layer.borderWidth = 1.0
        shareButton.layer.borderWidth = 1.0

        edgesForExtendedLayout = []
        extendedLayoutIncludesOpaqueBars = false
        if #available(iOS 11.0, *) {
            tableView.contentInsetAdjustmentBehavior = .never
        } else {
            automaticallyAdjustsScrollViewInsets = false
        }
        
        if isBookPremium && DatabaseManager.shared.isUserPremium() == false {
            becomeFriendView.isHidden = false
            becomeFriendLabel.text = "become_friend_desc".localized
            becomeFriendButton.text = "become_friend_button".localized.uppercased()
        }
        else {
            becomeFriendView.isHidden = true
        }
        
        refreshIsFavourite()
        refreshReadingState()
    }
    
    func createCells(bookDetails: BookDetailsModel) {
        cellsArray = [WLTableViewCell]()
        self.bookDetailsModel = bookDetails
        let titleCell = BookDetailsHeaderTableViewCell.instance(height: bigHeaderHeight)
        titleCell.setup(bookModel: bookDetails, topColor: topColor)
        cellsArray.append(titleCell)
        let infoCell = BookDetailsInfoTableViewCell.instance()
        infoCell.setup(bookModel: bookDetails)
        cellsArray.append(infoCell)
        cellsArray.append(BookDetailsSeparatorTableViewCell.instance())
        
        if bookDetails.fragmentHtml.count > 0{
            let fragmentCell = BookDetailsFragmentTableViewCell.instance()
            fragmentCell.setup(fragmentTitle: bookDetails.fragmentTitle, fragmentHtml: bookDetails.fragmentHtml)
            cellsArray.append( fragmentCell)
            cellsArray.append(BookDetailsSeparatorTableViewCell.instance())

        }
        
        if isBookPremium && DatabaseManager.shared.isUserPremium() == false{ // dont show buttons when user is not premium
            
        }
        else{
            
            if bookDetails.epub.count > 0{
                var buttonType = BookDetailsButtonType.download_ebook
                switch DownloadManager.sharedInstance.checkEbookStatus(bookSlug: bookDetails.slug){
                case .downloaded:
                    buttonType = .download_ebook_read
                case .downloading:
                    DownloadManager.sharedInstance.delegate = self
                    buttonType = .download_ebook_loading
                default:
                    break
                }
                readCell = BookDetailsButtonTableViewCell.instance(delegate: self, bookDetailsButtonType: buttonType, bookDetailsModel: bookDetails)
                cellsArray.append(readCell!)
            }
            
            if bookDetails.getAudiobookFilesUrls().count > 0 {
                var buttonType = BookDetailsButtonType.download_audiobook
                
                switch DownloadManager.sharedInstance.checkAudiobookStatus(bookDetailsModel: bookDetails){
                case .downloaded:
                    buttonType = .download_audiobook_listen
                case .downloading:
                    DownloadManager.sharedInstance.delegate = self
                    buttonType = .download_audiobook_loading
                default:
                    break
                }
                audiobookCell = BookDetailsButtonTableViewCell.instance(delegate: self, bookDetailsButtonType: buttonType, bookDetailsModel: bookDetails)
                cellsArray.append(audiobookCell!)
            }
            
//            cellsArray.append(BookDetailsButtonTableViewCell.instance(delegate: self, bookDetailsButtonType: .support_us, bookDetailsModel: bookDetails))
        }
    }
    
    func bookDetailsDownloaded(bookDetails: BookDetailsModel){
        self.bookDetailsModel = bookDetails
        tableView.isHidden = false
        buttonsContainer.isHidden = false
        headerView.alpha = 0
        topColor = self.bookDetailsModel!.bgColor
        headerLabel.text = bookDetails.title
        createCells(bookDetails: self.bookDetailsModel!)
        tableView.reloadData()
    }
    
    func setFavourite(favourite: Bool){
        isFavourite = favourite
        refreshFavouriteButton()
        
        syncManager.setFavouriteState(slug: bookSlug, favourite: favourite) {[weak self] (result) in
            
            guard let strongSelf = self else{
                return
            }
            
            strongSelf.refreshButton.setIndicatorButtonState(state: .hidden)
            switch result {
            case .success/*(let model)*/:
                strongSelf.isFavourite = favourite
            case .failure/*(let error)*/:
                
                break
            }
        }
    }
    
    func refreshFavouriteButton(){
        favouriteButton.setImage(isFavourite ? #imageLiteral(resourceName: "icon_heart-fill-big") : #imageLiteral(resourceName: "icon_heart-outline-big"), for: .normal)
    }
    
    func refreshIsFavourite(){
        guard syncManager.isLoggedIn() else { return }
        
        syncManager.getFavouriteState(slug: bookSlug) {[weak self] (result) in
            
            guard let strongSelf = self else{
                return
            }
            
            switch result {
            case .success(let model):
                strongSelf.isFavourite = (model as! LikeModel).likes
                strongSelf.refreshFavouriteButton()
            case .failure/*(let error)*/:
                break
            }
        }

    }
    
    func refreshReadingState(){
        
        guard syncManager.isLoggedIn() else {
            readingState = .unknown
            return
        }
        
        syncManager.getReadingState(slug: bookSlug, completionHandler: { [weak self] (result) in
        
            guard let strongSelf = self else{
                return
            }
            
            switch result {
            case .success(let model):
                strongSelf.readingState = (model as! ReadingStateModel).state
            case .failure/*(let error)*/:
                break
            }
        })
    }

    
    func refreshData(){
        var storedBook = false
        if let downloadedModel = DatabaseManager.shared.getBookFromDownloaded(bookSlug: bookSlug) {
            storedBook = true
            bookDetailsDownloaded(bookDetails: downloadedModel)
        }

        if storedBook == false {
            refreshButton.setIndicatorButtonState(state: .loading)
        }
        
        syncManager.getBookDetails(bookSlug: bookSlug) {[weak self] (result) in
            
            guard let strongSelf = self else{
                return
            }
            
            strongSelf.refreshButton.setIndicatorButtonState(state: .hidden)
            switch result {
            case .success(let model):
                if let model = model as? BookDetailsModel{
                    model.slug = strongSelf.bookSlug
                    if storedBook {
                        DatabaseManager.shared.addBookToDownloaded(bookDetailsModel: model)
                    }
                    strongSelf.bookDetailsDownloaded(bookDetails: model)
                }
                
            case .failure/*(let error)*/:
                if storedBook == false {
                    strongSelf.refreshButton.setIndicatorButtonState(state: .button)
                    self?.view.makeToast("book_loading_error".localized, duration: 3.0, position: .bottom)
                }
            }
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        openedPlayer = false
        navigationController?.setNavigationBarHidden(true, animated: true)
        DownloadManager.sharedInstance.delegate = self
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        if !openedPlayer {
            navigationController?.setNavigationBarHidden(false, animated: true)
        }
        DownloadManager.sharedInstance.delegate = nil
    }
    
    func downloadEbook(){
        guard let bookDetailsModel = bookDetailsModel, bookDetailsModel.epub.count > 0 else {return}
        
        DatabaseManager.shared.addBookToDownloaded(bookDetailsModel: bookDetailsModel)
        DownloadManager.sharedInstance.delegate = self
        DownloadManager.sharedInstance.downloadEbook(bookDetailsModel: bookDetailsModel)
    }
    
    var openedPlayer = false
    func downloadAudiobooks(){
        guard let bookDetailsModel = bookDetailsModel, bookDetailsModel.getAudiobookFilesUrls().count > 0 else {return}
        
        DatabaseManager.shared.addBookToDownloaded(bookDetailsModel: bookDetailsModel)

        DownloadManager.sharedInstance.delegate = self
        DownloadManager.sharedInstance.downloadAudiobooks(bookDetailsModel: bookDetailsModel)
    }

    func updateReadingStateIfNeeded(state: ReadingStateModel.ReadingState) {
        if state == .reading && readingState != .not_started {
            return
        }
        
        if state == .complete && readingState == .complete {
            return
        }

        syncManager.setReadingState(slug: bookSlug, readingState: state, completionHandler: nil)
    }
    
    func openFolioReader() {
        guard ebookExists(bookSlug: bookSlug) else {
            return
        }

        updateReadingStateIfNeeded(state: .reading)
        
        var array = parentNames()
        array.append("Reader")
        MatomoTracker.shared.track(view: array)

        let config = WLReaderConfig()
        let bookPath = FileType.ebook.pathForFileName(filename: bookSlug, bookSlug: bookSlug)
        let bookDirectory = FileType.ebook.destinationPathWithSlug(bookSlug: bookSlug) + "unzipped/"

        if !FileManager.default.fileExists(atPath: bookDirectory) {
            try! FileManager.default.createDirectory(atPath: bookDirectory, withIntermediateDirectories: true, attributes: nil)
        }

        let folioReader = FolioReader()
        folioReader.delegate = self
        
        folioReader.presentReader(parentViewController: self.navigationController!, withEpubPath: bookPath, unzipPath: bookDirectory, andConfig: config, shouldRemoveEpub: false)
    }
    
    func openAudiobook(afterDownload: Bool) {
        guard let bookDetailsModel = bookDetailsModel else { return }
        
        updateReadingStateIfNeeded(state: .reading)

        openedPlayer = true
        navigationController?.pushViewController(PlayerViewController.instance(bookDetailsModel: bookDetailsModel), animated: true)
    }
    
    deinit {
    }
}

extension BookDetailsViewController: UITableViewDataSource{
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return cellsArray.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return cellsArray[indexPath.row]
    }
}

extension BookDetailsViewController: UITableViewDelegate{
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return cellsArray[indexPath.row].getHeight()
    }
    
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        print(scrollView.contentOffset)
        
        var progress: CGFloat = 100

        var newValue = bigHeaderHeight - scrollView.contentOffset.y
        if newValue < smallHeaderHeight{
            newValue = smallHeaderHeight
            progress = 0
        }
        else{
            progress = (bigHeaderHeight - smallHeaderHeight - scrollView.contentOffset.y) / (bigHeaderHeight - smallHeaderHeight)
            if progress > 100{
                progress = 100
            }
        }
        
        headerView.alpha = 1 - progress
        buttonsContainer.alpha = progress
        headerHeightConstraint.constant = newValue
        headerProgress = progress
    }
    
    func showHeader(){
        headerView.alpha = 1
        buttonsContainer.alpha = 0
        headerHeightConstraint.constant = smallHeaderHeight
        headerProgress = 0
    }
}

extension BookDetailsViewController: BookDetailsButtonTableViewCellDelegate{
    
    func bookDetailsButtonTableViewCellButtonTapped(buttonType: BookDetailsButtonType){
        switch buttonType {
        case .download_ebook:
            downloadEbook()
        case .download_ebook_read:
            openFolioReader()
        case .download_audiobook:
            downloadAudiobooks()
        case .download_audiobook_listen:
            openAudiobook(afterDownload: false)
        default:
            break
        }
    }
    
    func bookDetailsButtonTableViewCellDeleteButtonTapped(buttonType: BookDetailsButtonType){
        switch buttonType {
        case .download_ebook_read, .download_ebook_loading:
            DownloadManager.sharedInstance.deleteEbook(bookSlug: bookSlug)
            readCell?.setup(bookDetailsButtonType: .download_ebook, progress: nil, bookDetailsModel: bookDetailsModel)
            bookDetailsModel = (bookDetailsModel?.copy() as! BookDetailsModel)
            let _ = DatabaseManager.shared.removeBookFromDownloaded(bookSlug: bookSlug)
        case .download_audiobook_listen, .download_audiobook_loading:
            if let bookDetails = PlayerController.shared.currentBookDetails, bookDetails.slug == bookSlug{
                PlayerController.shared.stopAndClear()
            }
            DownloadManager.sharedInstance.clearDownloadingAudiobookFromQueue(bookSlug: bookSlug)
            DownloadManager.sharedInstance.deleteAudiobook(bookSlug: bookSlug)
            audiobookCell?.setup(bookDetailsButtonType: .download_audiobook, progress:nil, bookDetailsModel: bookDetailsModel)
            bookDetailsModel = (bookDetailsModel?.copy() as! BookDetailsModel)
            let _ = DatabaseManager.shared.removeBookFromDownloaded(bookSlug: bookSlug)
        default:
            break
        }
    }
}

extension BookDetailsViewController: DownloadManagerDelegate{
    
    func downloadManagerDownloadProgressChanged(model: MZDownloadModel, allProgress: Float, bookSlug: String) {
        guard let bookDetailsModel = bookDetailsModel, bookDetailsModel.slug == bookSlug else { return        }

        if model.isAudiobook() {
            audiobookCell?.setup(bookDetailsButtonType: .download_audiobook_loading, progress: allProgress,  bookDetailsModel: bookDetailsModel)
        }
        else if model.isEbook() {
            readCell?.setup(bookDetailsButtonType: .download_ebook_loading, progress: model.progress,  bookDetailsModel: bookDetailsModel)
        }
    }
    
    func downloadManagerDownloadFinished(model: MZDownloadModel, bookSlug: String) {
        guard let bookDetailsModel = bookDetailsModel, bookDetailsModel.slug == bookSlug else { return        }
        
        if model.isAudiobook() {
            
            bookDetailsModel.setInitialAudiobookChaptersValuesIfNeeded()
            
            audiobookCell?.setup(bookDetailsButtonType: .download_audiobook_listen, progress: nil,  bookDetailsModel: bookDetailsModel)
            openAudiobook(afterDownload: true)
        }
        else if model.isEbook() {
            readCell?.setup(bookDetailsButtonType: .download_ebook_read, progress: nil,  bookDetailsModel: bookDetailsModel)
            openFolioReader()
        }
    }
    
    func downloadManagerDownloadFailed(model: MZDownloadModel, bookSlug: String) {
        guard let bookDetailsModel = bookDetailsModel, bookDetailsModel.slug == bookSlug else { return }

        if model.isAudiobook() {
            view.makeToast("audiobook_download_error".localized, duration: 3.0, position: .bottom)
            audiobookCell?.setup(bookDetailsButtonType: .download_audiobook, progress: nil, bookDetailsModel: bookDetailsModel)
        }
        else if model.isEbook() {
            view.makeToast("book_download_error".localized, duration: 3.0, position: .bottom)
            readCell?.setup(bookDetailsButtonType: .download_ebook, progress: nil, bookDetailsModel: bookDetailsModel)
        }
    }
}

extension BookDetailsViewController: FolioReaderDelegate{
    @objc func folioReaderDidClose(_ folioReader: FolioReader) {
//        guard let bookDetailsModel = bookDetailsModel, let progressValues = folioReader.getProgressValues(), progressValues.currentPage > 0, progressValues.currentPage <= progressValues.totalPages else { return }
//        
//        DatabaseManager.shared.updateCurrentChapters(bookDetailsModel: bookDetailsModel, currentChapter: progressValues.currentPage, totalChapters: progressValues.totalPages, currentAudioChapter: nil, totalAudioChapters: nil)
//        readCell?.setup(bookDetailsButtonType: .download_ebook_read, progress: nil, bookDetailsModel: bookDetailsModel)
//        
//        if progressValues.currentPage == progressValues.totalPages && readingState == ReadingStateModel.ReadingState.reading{
//           updateReadingStateIfNeeded(state: .complete)
//        }
    }
}
