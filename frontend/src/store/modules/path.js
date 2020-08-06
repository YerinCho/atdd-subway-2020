import PathService from '@/api/modules/path'

const state = {
  pathResult: null
}

const getters = {
  pathResult(state) {
    return state.pathResult
  }
}

const mutations = {
  setPath(state, pathResult) {
    state.pathResult = pathResult
  }
}

const actions = {
  async searchPath({commit}, item) {
    return PathService.get(item.source, item.target, item.type).then(({data}) => {
      commit('setPath', data)
    })
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
