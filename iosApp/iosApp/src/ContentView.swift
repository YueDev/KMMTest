import SwiftUI
import shared
import Kingfisher

struct ContentView: View {

    private let collage = Collage()
    @State private var result: TCResult? = nil

    var body: some View {
        GeometryReader { geo in
            ZStack {
                getCollageView(result: result)
                VStack {
                    Spacer().frame(maxHeight: .infinity)
                    Button.init {
                            result = collage.collage(width: geo.size.width, height: geo.size.height, padding: 0.0)
                        } label: {
                            Text("collage")
                        }
                        .buttonStyle(.borderedProminent)
                    Spacer().frame(height: 8)
                }
            }
        }.padding(16)
    }

}

extension ContentView {
    func getCollageView(result: TCResult?) -> some View {
        ZStack(alignment: .topLeading) {
            Color.green.opacity(0.2)
            if let result = result,
               let images = collage.images {

                ForEach(images, id: \.tcBitmap.uuid) { image in

                    let id = image.tcBitmap.uuid
                    let rect = result.get(uuid: id)!
                    let width = rect.right - rect.left
                    let height = rect.bottom - rect.top
                    let offsetX = rect.left
                    let offsetY = rect.top

                    Image(uiImage: image.image)
                        .resizable()
                        //这句要写在尺寸前边才是center crop的效果
                        .aspectRatio(contentMode: .fill)
                        .frame(width: CGFloat(width), height: CGFloat(height))
                        .offset(x: CGFloat(offsetX), y: CGFloat(offsetY))
                }
                    .animation(.easeInOut(duration: 0.5), value: result)
            }
        }

    }
}


class Collage {
    private let IMAGE_MAX_SIZE = 1080

    private let tcCollage = TCCollage()
    var images: [CollageImage]? = nil

    func collage(width: Double, height: Double, padding: Double) -> TCResult? {
        initData()
        guard let images = images else {
            return nil
        }
        tcCollage.doInit(bitmaps: images.map(\.tcBitmap))
        return tcCollage.collage(width: width, height: height, padding: padding)
    }

    func initData() {
        if images == nil {
            images = []
            (1...9).forEach {
                guard let image = UIImage.init(named: "test\($0)")?.centerInside(width: 1080, height: 1080) else {
                    return
                }
                images?.append(image.toCollageImage())
            }
        }
    }
}

class CollageImage {
    let image: UIImage
    let tcBitmap: TCBitmap

    init(_ image: UIImage, _ tcBitmap: TCBitmap) {
        self.image = image
        self.tcBitmap = tcBitmap
    }
}

fileprivate extension UIImage {
    func toCollageImage() -> CollageImage {
        let tcBitmap = TCBitmap.init(uuid: UUID().uuidString, width: Int32(size.width), height: Int32(size.height))
        return CollageImage(self, tcBitmap)
    }
}


struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
