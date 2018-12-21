//
//  SearchViewFiltersManager.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 16/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

protocol SearchFiltersManagerDelegate: class {
    func searchFiltersManagerFiltersChanged(reloadData: Bool)
}

class SearchFiltersManager: NSObject{
    
    init(delegate: SearchFiltersManagerDelegate) {
        self.delegate = delegate
    }
    
    weak var delegate: SearchFiltersManagerDelegate?
    
    enum FilterType{
        case onlyLectures
        case hasAudiobook
        case epochs
        case kinds
        case genres
    }
    
    lazy var onlyLecturesCategory : CategoryModel = {
        var mdl = CategoryModel()
        mdl.name = "only_lecture".localized
        mdl.slug = "only_lectures_slug"
        mdl.checked = false
        return mdl
    }()

    lazy var hasAudiobookCategory : CategoryModel = {
        var mdl = CategoryModel()
        mdl.name = "has_audiobook".localized
        mdl.slug = "has_audiobook_slug"
        mdl.checked = false
        return mdl
    }()

    var selectedKindsArray = [CategoryModel]()
    var selectedEpochsArray = [CategoryModel]()
    var selectedGenresArray = [CategoryModel]()
    
    func numberOfFilters() -> Int{
        return (onlyLecturesCategory.checked ? 1 : 0) + (hasAudiobookCategory.checked ? 1 : 0) + selectedKindsArray.count + selectedGenresArray.count + selectedEpochsArray.count
    }
    
    func removeFilter(atIndex: Int) {
        
        if let obj = getFilterTypeAndIndex(forIndex: atIndex){
                
            print("removeFilter getFilterType \(obj.0) AndIndex \(obj.1)")
            switch obj.0{
            case .onlyLectures:
                onlyLecturesCategory.checked = false
            case .hasAudiobook:
                hasAudiobookCategory.checked = false
            case .kinds:
                print("remove selectedKindsArray.count: \(selectedKindsArray.count), index: \(obj.1)")
                selectedKindsArray.remove(at: obj.1)
            case .epochs:
                print("remove selectedEpochsArray.count: \(selectedEpochsArray.count), index: \(obj.1)")
                selectedEpochsArray.remove(at: obj.1)
            case .genres:
                print("remove selectedGenresArray.count: \(selectedGenresArray.count), index: \(obj.1)")
                selectedGenresArray.remove(at: obj.1)
            }
        }
    }
    
    func getFilterArrayAndIndex(forIndex: Int) -> ([CategoryModel], Int)? {
        if let obj = getFilterTypeAndIndex(forIndex: forIndex){
            print("FilterArrayAndIndex getFilterType \(obj.0) AndIndex \(obj.1)")

            switch obj.0{
            case .onlyLectures:
                return ([onlyLecturesCategory], 0)
            case .hasAudiobook:
                return ([hasAudiobookCategory], 0)
            case .kinds:
                print("selectedKindsArray.count: \(selectedKindsArray.count), index: \(obj.1)")
                return (selectedKindsArray, obj.1)
            case .epochs:
                print("selectedEpochsArray.count: \(selectedEpochsArray.count), index: \(obj.1)")
                return (selectedEpochsArray, obj.1)
            case .genres:
                print("selectedGenresArray.count: \(selectedGenresArray.count), index: \(obj.1)")
                return (selectedGenresArray, obj.1)
            }
        }
        return nil
    }
    
    func getFilterTypeAndIndex(forIndex: Int) -> (FilterType, Int)? {
        
        var index = forIndex
        if onlyLecturesCategory.checked{
            if forIndex == 0{
                return (.onlyLectures, 0)
            }
            else{
                index -= 1
            }
        }
        
        if hasAudiobookCategory.checked {
            if forIndex == 0 || (forIndex == 1 && onlyLecturesCategory.checked){
                return (.hasAudiobook, 0)
            }
            else {
                index -= 1
            }
        }
        
        if index < selectedEpochsArray.count{
            return (.epochs, index)
        }
        else if index < (selectedEpochsArray.count + selectedKindsArray.count){
            return (.kinds, index - selectedEpochsArray.count)
        }
        else if index < (selectedEpochsArray.count + selectedKindsArray.count + selectedGenresArray.count){
            return (.genres, index - selectedEpochsArray.count - selectedKindsArray.count )
        }

        return nil
    }
    
    func getFilter(forIndex: Int) -> CategoryModel? {
        
        if let obj = getFilterArrayAndIndex(forIndex: forIndex){
            return obj.0[obj.1]
        }
        return nil
    }
    
    func getFilterForApi(filterType: FilterType) -> String? {
        
        var value: String = ""
        switch filterType {
        case .onlyLectures:
            return onlyLecturesCategory.slug
        case .hasAudiobook:
            return hasAudiobookCategory.slug
        case .epochs:
            value = selectedEpochsArray.map({$0.slug}).joined(separator: ",")
        case .genres:
            value = selectedGenresArray.map({$0.slug}).joined(separator: ",")
        case .kinds:
            value = selectedKindsArray.map({$0.slug}).joined(separator: ",")
        }
        
        return value.count > 0 ? value : nil
    }
    
    func getParametersForApi() -> FilterBooksParameters {
        let params = FilterBooksParameters()
        params.epochs = getFilterForApi(filterType: .epochs)
        params.genres = getFilterForApi(filterType: .genres)
        params.kinds = getFilterForApi(filterType: .kinds)
        params.onlyLectures = onlyLecturesCategory.checked
        params.hasAudiobook = hasAudiobookCategory.checked

        return params
    }
    
    func clearFilters() {
        selectedKindsArray = [CategoryModel]()
        selectedEpochsArray = [CategoryModel]()
        selectedGenresArray = [CategoryModel]()
        onlyLecturesCategory.checked = false
        hasAudiobookCategory.checked = false
        delegate?.searchFiltersManagerFiltersChanged(reloadData: true)
    }
}

extension SearchFiltersManager: UICollectionViewDataSource{
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int{
        let numerOfFilters = numberOfFilters()
        print("numerOfFilters += \(numerOfFilters)")
        return numerOfFilters
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell{
        
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "SearchFilterCollectionViewCell", for: indexPath) as! SearchFilterCollectionViewCell
        print("beforeGetFilter")
        if let filter = getFilter(forIndex: indexPath.row){
            cell.setup(categoryModel: filter)
        }
        print("afterGetFilter")
        return cell
    }
}

extension SearchFiltersManager: UICollectionViewDelegate{
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        print("didSelectItems.row += \(indexPath.row)")
        
        removeFilter(atIndex: indexPath.row)
        collectionView.deleteItems(at: [indexPath])
        
        delegate?.searchFiltersManagerFiltersChanged(reloadData: numberOfFilters() == 0)
    }
}

extension SearchFiltersManager: UICollectionViewDelegateFlowLayout {
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        var size = CGSize(width: 200, height: 44)
        if let filter = getFilter(forIndex: indexPath.row){
            
            size.width = filter.name.sizeOf(UIFont.systemFont(ofSize: 10, weight: .medium)).width + 14 + 35
        }
        return size
    }
}
