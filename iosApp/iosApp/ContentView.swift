import SwiftUI
import shared

struct ContentView: View {

    @State private var imageClient = ImageClient.Loading

    var body: some View {
        ZStack {
            switch imageClient {
            case .Loading:
                getButtonView()
                getLoadingView()
            case .Success(let urls):
                getImageListView(urls)
                getButtonView()
            case .Error(let error):
                getErrorView(error)
                getButtonView()
            }
        }
            .onAppear {
                requestImages()
            }
    }

}


extension ContentView {

    private func getErrorView(_ error: String) -> some View {
        Text(error)
    }

    private func getImageListView(_ urls: [String]) -> some View {
        Text(urls.description)
    }

    private func getButtonView() -> some View {
        VStack {
            Spacer()
            Button {
                requestImages()
            } label: {
                Text("Refresh")
            }
                .buttonStyle(.borderedProminent)
        }
    }

    private func getLoadingView() -> some View {
        ZStack {
            Spacer().frame(maxWidth: .infinity, maxHeight: .infinity)
            ProgressView()
        }
            .background(Color(red: 0.0, green: 0.0, blue: 0.0, opacity: 0.5))
    }

}


extension ContentView {

    private func requestImages() {
        imageClient = ImageClient.Loading
        Task {
            do {
                let urls = try await getImagesFromRepository()
                imageClient = ImageClient.Success(imageUrls: urls)
            } catch {
                imageClient = ImageClient.Error(error: error.localizedDescription)
            }
        }
    }

    @MainActor
    private func getImagesFromRepository() async throws -> [String] {
        try await ImageClient.repository.getImageUrls()
    }

}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
