//
//  FilterViewController.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import Toast_Swift

enum FilterSection: Int{
    case onlyLectures = 0
    case hasAudiobook
    case epochs
    case kinds
    case genres
    
    var title: String{
        return "filter_\(self)".localized.uppercased()
    }
    
    var failedText: String{
        return "load_\(self)_failed".localized
    }
}

class LeftAlignedCollectionViewFlowLayout: UICollectionViewFlowLayout {
    
    override init() {
        super.init()
        minimumLineSpacing = 0
        
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutAttributesForElements(in rect: CGRect) -> [UICollectionViewLayoutAttributes]? {
        let attributes = super.layoutAttributesForElements(in: rect)
        
        var leftMargin = sectionInset.left
        var maxY: CGFloat = -1.0
        attributes?.forEach { layoutAttribute in
            
            if(layoutAttribute.representedElementCategory == .cell){
                if layoutAttribute.frame.origin.y >= maxY {
                    leftMargin = sectionInset.left
                }
                
                layoutAttribute.frame.origin.x = leftMargin
                
                leftMargin += layoutAttribute.frame.width + minimumInteritemSpacing
                maxY = max(layoutAttribute.frame.maxY , maxY)
            }
        }
        return attributes
    }
}

protocol FilterViewControllerDelegate: class {
    func filterViewControllerDidSelectItems(kindsArray: [CategoryModel], epochsArray: [CategoryModel], genresArray: [CategoryModel], onlyLectures: Bool, hasAudiobook: Bool, filterChanged: Bool)
}

class FilterViewController: WLViewController {
    
    class func instance(delegate: FilterViewControllerDelegate, selectedKindsArray: [CategoryModel], selectedEpochsArray: [CategoryModel], selectedGenresArray: [CategoryModel], onlyLectures: Bool, hasAudiobook: Bool) -> FilterViewController{
        let controller = FilterViewController.instance()
        controller.initialSelectedKindsArray = selectedKindsArray
        controller.initialSelectedEpochsArray = selectedEpochsArray
        controller.initialSelectedGenresArray = selectedGenresArray
        controller.onlyLectures = onlyLectures
        controller.hasAudiobook = hasAudiobook
        controller.delegate = delegate
        return controller
    }
    
    weak var delegate: FilterViewControllerDelegate!
    @IBOutlet weak var collectionView: UICollectionView!

    var initialSelectedKindsArray: [CategoryModel]!
    var initialSelectedEpochsArray: [CategoryModel]!
    var initialSelectedGenresArray: [CategoryModel]!

    var kindsArray: [CategoryModel]?
    var epochsArray: [CategoryModel]?
    var genresArray: [CategoryModel]?
    
    var kindsSection: FilterSectionHeaderCollectionReusableView?
    var epochsSection: FilterSectionHeaderCollectionReusableView?
    var genresSection: FilterSectionHeaderCollectionReusableView?
    
    var isKindsDownloading = false
    var isEpochsDownloading = false
    var isGenresDownloading = false
    
    var filterChanged = false
    var onlyLectures = false
    var hasAudiobook = false

    override func viewDidLoad() {
        super.viewDidLoad()

        title = "filters".localized
        
        setupCollectionView()
        
        getData(filterSection: .epochs)
        getData(filterSection: .kinds)
        getData(filterSection: .genres)
    }
    
    @IBAction func closeAction(_ sender: Any) {
        navigationController?.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func confirmAction(_ sender: Any) {
        delegate.filterViewControllerDidSelectItems(kindsArray: kindsArray?.filter({$0.checked == true}) ?? [CategoryModel](), epochsArray: epochsArray?.filter({$0.checked == true}) ?? [CategoryModel](), genresArray: genresArray?.filter({$0.checked == true}) ?? [CategoryModel](), onlyLectures: onlyLectures, hasAudiobook: hasAudiobook, filterChanged: filterChanged)
    }
    
    func getSection(filterSection: FilterSection) -> FilterSectionHeaderCollectionReusableView? {
        switch filterSection {
        case .epochs:
            return epochsSection
        case .genres:
            return genresSection
        case .kinds:
            return kindsSection
        default:
            return nil
        }
    }
    
    func setDataSource(filterSection:FilterSection, dSource: [CategoryModel]?) {
        switch filterSection {
        case .epochs:
            if let arr = dSource{
                for obj in initialSelectedEpochsArray{
                    arr.first(where: {$0.slug == obj.slug})?.checked = true
                }
            }
            epochsArray = dSource
        case .genres:
            if let arr = dSource{
                for obj in initialSelectedGenresArray{
                    arr.first(where: {$0.slug == obj.slug})?.checked = true
                }
            }
            genresArray = dSource
        case .kinds:
            if let arr = dSource{
                for obj in initialSelectedKindsArray{
                    arr.first(where: {$0.slug == obj.slug})?.checked = true
                }
            }
            kindsArray = dSource
        default:
            break
        }
    }
    
    func setIsDownloading(isDownloading: Bool, section: FilterSection){
        switch section {
        case .epochs:
            isEpochsDownloading = isDownloading
        case .genres:
            isGenresDownloading = isDownloading
        case .kinds:
            isKindsDownloading = isDownloading
        default:
            break
        }
    }

    func getIsDownloading(section: FilterSection) -> Bool{
        switch section {
        case .epochs:
            return isEpochsDownloading
        case .genres:
            return isGenresDownloading
        case .kinds:
            return isKindsDownloading
        default:
            return false
        }
    }

    func getData(filterSection: FilterSection){
        
        getSection(filterSection: filterSection)?.refreshButton.setIndicatorButtonState(state: .loading)
        setIsDownloading(isDownloading: true, section: filterSection)
        
        syncManager.getCategories(filterSection: filterSection, bookOnly: true) { [weak self] (result) in
            self?.setIsDownloading(isDownloading: false, section: filterSection)
            switch result {
            case .success(let model):
                self?.getSection(filterSection: filterSection)?.refreshButton.setIndicatorButtonState(state: .hidden)
                self?.setDataSource(filterSection: filterSection, dSource: model as? [CategoryModel])
                self?.collectionView.reloadSections([filterSection.rawValue])
            case .failure/*(let *error)*/:
                self?.getSection(filterSection: filterSection)?.refreshButton.setIndicatorButtonState(state: .button)
                self?.view.makeToast(filterSection.failedText, duration: 3.0, position: .bottom)
            }
        }
    }
    
    func setupCollectionView(){
        
        collectionView.backgroundColor = UIColor.clear
        collectionView.delegate = self
        collectionView.dataSource = self
        collectionView.register(UINib.init(nibName: "FilterCollectionViewCell", bundle: nil), forCellWithReuseIdentifier: "FilterCollectionViewCell")
        collectionView.register(UINib.init(nibName: "FilterSectionHeaderCollectionReusableView", bundle: nil), forSupplementaryViewOfKind: UICollectionElementKindSectionHeader, withReuseIdentifier: "FilterSectionHeaderCollectionReusableView")
        collectionView.register(UINib.init(nibName: "FilterOnlyLecturesReusableView", bundle: nil), forSupplementaryViewOfKind: UICollectionElementKindSectionHeader, withReuseIdentifier: "FilterOnlyLecturesReusableView")
        
        if #available(iOS 11.0, *) {
            collectionView.contentInsetAdjustmentBehavior = .always
        }
        
        collectionView.setCollectionViewLayout(LeftAlignedCollectionViewFlowLayout(), animated: true)
    }
    
    func getArrayForSection(filterSection: FilterSection) -> [CategoryModel]?{
        switch filterSection{
        case .onlyLectures, .hasAudiobook:
            return nil
        case .epochs:
            return epochsArray
        case .genres:
            return genresArray
        case .kinds:
            return kindsArray
        }
    }
    
    func numberOfRowsInSection(filterSection: FilterSection) -> Int{
        return getArrayForSection(filterSection: filterSection)?.count ?? 0
    }
    
    func clearSectionHeaderReference(section: FilterSectionHeaderCollectionReusableView) {
        if genresSection == section{
            genresSection = nil
        }
        if epochsSection == section{
            epochsSection = nil
        }
        if kindsSection == section{
            kindsSection = nil
        }
    }
}

extension FilterViewController: UICollectionViewDelegateFlowLayout{
   
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        
        if let items = getArrayForSection(filterSection: FilterSection(rawValue: indexPath.section)!){
            let item = items[indexPath.row]
            return CGSize(width: item.name.uppercased().width(withConstrainedHeight: 20, font: UIFont.systemFont(ofSize: 14, weight: .medium)) + 40, height: 30)
        }
        return CGSize(width: 50, height: 30)
    }
    
    func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        
        if indexPath.section == 0{
            let sectionHeader = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: "FilterOnlyLecturesReusableView", for: indexPath) as? FilterOnlyLecturesReusableView
            sectionHeader?.delegate = self
            sectionHeader?.onSwitch.isOn = onlyLectures
            sectionHeader?.setup(isAudiobook: false)
            return sectionHeader!
        }
        if indexPath.section == 1{
            let sectionHeader = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: "FilterOnlyLecturesReusableView", for: indexPath) as? FilterOnlyLecturesReusableView
            sectionHeader?.delegate = self
            sectionHeader?.onSwitch.isOn = hasAudiobook
            sectionHeader?.setup(isAudiobook: true)
            return sectionHeader!
        }

        else{
            let sectionHeader = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: "FilterSectionHeaderCollectionReusableView", for: indexPath) as! FilterSectionHeaderCollectionReusableView
            clearSectionHeaderReference(section: sectionHeader)
            
            let filterSection = FilterSection(rawValue: indexPath.section)!
            sectionHeader.setup(filterSection: filterSection, isDownloading: getIsDownloading(section: filterSection))
            sectionHeader.delegate = self

            if indexPath.section == FilterSection.epochs.rawValue{
                epochsSection = sectionHeader
            }
            else if indexPath.section == FilterSection.genres.rawValue{
                genresSection = sectionHeader
            }
            else if indexPath.section == FilterSection.kinds.rawValue{
                kindsSection = sectionHeader
            }
            return sectionHeader
        }
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, referenceSizeForHeaderInSection section: Int) -> CGSize {
        
        if section == 0 || section == 1{
            return CGSize(width: UIScreen.main.bounds.width, height: 44)
        }
        else{
            if getArrayForSection(filterSection: FilterSection(rawValue: section)!) != nil{
                return CGSize(width: UIScreen.main.bounds.width, height: 44)
            }
            else{
                return CGSize(width: UIScreen.main.bounds.width, height: 180)
            }
        }
    }
}

extension FilterViewController: UICollectionViewDataSource{ //UICollectionViewDataSource
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 5
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int{
        return numberOfRowsInSection(filterSection: FilterSection(rawValue: section)!)
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell{
        
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "FilterCollectionViewCell", for: indexPath) as! FilterCollectionViewCell
        
        if let items = getArrayForSection(filterSection: FilterSection(rawValue: indexPath.section)!){
            cell.setup(categoryModel: items[indexPath.row])
        }
        return cell
    }
}

extension FilterViewController: UICollectionViewDelegate{
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        
        if let items = getArrayForSection(filterSection: FilterSection(rawValue: indexPath.section)!), items.count > indexPath.row{
            filterChanged = true
            let item = items[indexPath.row]
            item.checked = !item.checked
            if let cell = collectionView.cellForItem(at: indexPath) as? FilterCollectionViewCell{
                cell.setChecked(value: item.checked)
            }
        }
    }
}

extension FilterViewController: FilterOnlyLecturesReusableViewDelegate{
    func filterOnlyLecturesReusableViewSwitchValueChanged(value: Bool, isAudiobook: Bool){
        if isAudiobook {
            hasAudiobook = value
        }
        else {
            onlyLectures = value
        }
        filterChanged = true
    }
}

extension FilterViewController: FilterSectionHeaderCollectionReusableViewDelegate{
    func filterSectionRefreshButtonTapped(section: FilterSection){
        getData(filterSection: section)
    }
}
