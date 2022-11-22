//
// Created by YUE on 2022/11/20.
// Copyright (c) 2022 orgName. All rights reserved.
//

import Foundation
import shared

enum ImageClient {

    static let repository = ImageRepository()
    case Loading
    case Success(imageUrls: [String])
    case Error(error: String)


}
